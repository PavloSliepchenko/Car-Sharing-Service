package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.payment.CreatePaymentRequestDto;
import carsharing.carsharingservice.dto.rental.ActualReturnDateDto;
import carsharing.carsharingservice.dto.rental.CreateRentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalActiveStatusDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalResponseFullInfoDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.exception.EntityNotFoundException;
import carsharing.carsharingservice.exception.RentalException;
import carsharing.carsharingservice.mapper.RentalMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.NotificationService;
import carsharing.carsharingservice.service.PaymentService;
import carsharing.carsharingservice.service.RentalService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Payment.Status PENDING = Payment.Status.PENDING;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Override
    public RentalResponseDto addNewRental(Long userId, CreateRentalRequestDto requestDto) {
        List<Rental> activeRental = rentalRepository.findByUserIdAndActive(userId, true);
        if (activeRental.size() > 0) {
            throw new RentalException("You have an active rental. "
                    + "You can't have more than one rental");
        }

        Optional<Car> carOptional = carRepository.findById(requestDto.getCarId());
        if (carOptional.isEmpty()) {
            throw new EntityNotFoundException("There is no car with id " + requestDto.getCarId());
        }

        List<Payment> pendingPayments = paymentRepository.findByUserIdAndStatus(userId, PENDING);
        if (!pendingPayments.isEmpty()) {
            throw new RentalException("You have pending payments. Please pay it first. Rental id "
                    + pendingPayments.get(0).getRental().getId());
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate rentalDate = LocalDate.parse(requestDto.getRentalDate(), FORMATTER);
        if (rentalDate.isBefore(currentDate)) {
            throw new RentalException("Rental date cannot be before current date");
        }
        Car car = carOptional.get();
        if (car.getInventory() == 0) {
            throw new RentalException(String.format("Car %s is not available at the moment",
                    car.getModel()));
        }
        car.setInventory(car.getInventory() - 1);
        Car updatedCar = carRepository.save(car);
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setCar(updatedCar);
        rental.setUser(userRepository.findById(userId).get());
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalResponseDto> getRentalsByParameters(RentalSearchParametersDto paramsDto) {
        return rentalRepository.findByUserIdAndActive(paramsDto.userId(), paramsDto.isActive())
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public List<RentalResponseFullInfoDto> getRentalsByStatus(RentalActiveStatusDto requestDto) {
        return rentalRepository.findAllByActive(requestDto.isActive()).stream()
                .map(rentalMapper::toFullInfoDto)
                .toList();
    }

    @Override
    public RentalResponseDto getRentalById(Long userId, Long rentalId) {
        return rentalMapper.toDto(rentalRepository.findByIdAndUserId(rentalId, userId).orElseThrow(
                () -> new EntityNotFoundException("You don't have rental with id " + rentalId)
        ));
    }

    @Override
    public RentalResponseDto setActualReturnDate(Long userId, ActualReturnDateDto returnDateDto) {
        Rental rental = rentalRepository.findByIdAndUserId(returnDateDto.rentalId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("You don't have rental with id "
                        + returnDateDto.rentalId())
                );
        if (!rental.isActive()) {
            throw new RuntimeException("This rental has been returned before");
        }
        rental.setActualReturnDate(LocalDate.now());
        rental.setActive(false);
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        payForRental(rental);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        List<RentalResponseFullInfoDto> overdueRents = rentalRepository.findAllByActive(true)
                .stream()
                .filter(e -> e.getActualReturnDate() == null && e.getReturnDate().isBefore(today))
                .map(rentalMapper::toFullInfoDto)
                .toList();
        notificationService.sendNotification(overdueRents);
    }

    private void payForRental(Rental rental) {
        paymentService.setPayment(new CreatePaymentRequestDto(rental.getId(),
                Payment.Type.PAYMENT.name()));
        if (rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
            paymentService.setPayment(new CreatePaymentRequestDto(rental.getId(),
                    Payment.Type.FINE.name()));
        }
    }
}
