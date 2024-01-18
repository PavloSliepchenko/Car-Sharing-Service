package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.dto.payment.CreatePaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.rental.ActualReturnDateDto;
import carsharing.carsharingservice.dto.rental.CreateRentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalActiveStatusDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalResponseFullInfoDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.exception.EntityNotFoundException;
import carsharing.carsharingservice.exception.RentalException;
import carsharing.carsharingservice.mapper.RentalMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.impl.RentalServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalMapper rentalMapper;
    private Car car;
    private CarResponseDto carResponseDto;
    private Rental rental;
    private Rental rental2;
    private RentalResponseDto rentalResponseDto;
    private RentalResponseDto rentalResponseDto2;
    private User user;
    private CreateRentalRequestDto rentalRequestDto;

    @BeforeEach
    void init() {
        car = new Car();
        car.setId(2L);
        car.setBrand("BMW");
        car.setType(Car.CarType.SEDAN);
        car.setModel("M5");
        car.setInventory(7);
        car.setDailyFee(BigDecimal.valueOf(50));

        carResponseDto = new CarResponseDto();
        carResponseDto.setId(car.getId());
        carResponseDto.setBrand(car.getBrand());
        carResponseDto.setType(carResponseDto.getType());
        carResponseDto.setModel(car.getModel());
        carResponseDto.setDailyFee(car.getDailyFee());

        user = new User();
        user.setId(1L);
        user.setEmail("user@user.com");
        user.setFirstName("User");
        user.setLastName("User");
        user.setRole(User.Role.CUSTOMER);
        user.setPassword("1234");

        rentalRequestDto = new CreateRentalRequestDto();
        LocalDate rentalDate = LocalDate.now();
        LocalDate returnDate = rentalDate.plusDays(3);
        rentalRequestDto.setRentalDate(rentalDate.format(FORMATTER));
        rentalRequestDto.setReturnDate(returnDate.format(FORMATTER));
        rentalRequestDto.setCarId(car.getId());

        rental = new Rental();
        rental.setCar(car);
        rental.setId(1L);
        rental.setActive(true);
        rental.setRentalDate(rentalDate);
        rental.setReturnDate(returnDate);

        rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.setId(rental.getId());
        rentalResponseDto.setCar(carResponseDto);
        rentalResponseDto.setRentalDate(rental.getRentalDate().format(FORMATTER));
        rentalResponseDto.setReturnDate(rental.getReturnDate().format(FORMATTER));

        rental2 = new Rental();
        rental2.setUser(user);
        rental2.setId(2L);
        rental2.setCar(car);
        rental2.setRentalDate(rental.getRentalDate().minusDays(5));
        rental2.setReturnDate(rental.getRentalDate().minusDays(2));
        rental2.setActualReturnDate(rental2.getReturnDate());
        rental2.setActive(false);

        rentalResponseDto2 = new RentalResponseDto();
        rentalResponseDto2.setRentalDate(rental2.getRentalDate().format(FORMATTER));
        rentalResponseDto2.setReturnDate(rental2.getReturnDate().format(FORMATTER));
        rentalResponseDto2.setActualReturnDate(rental2.getActualReturnDate().format(FORMATTER));
        rentalResponseDto2.setId(2L);
        rentalResponseDto2.setCar(carResponseDto);
    }

    @Test
    @DisplayName("Add new rental")
    void addNewRental_ValidRequestDto_ShouldReturnResponseDto() {
        Mockito.when(rentalRepository.findByUserIdAndActive(user.getId(), true))
                .thenReturn(new ArrayList<>());
        Mockito.when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        Mockito.when(paymentRepository.findByUserIdAndStatus(user.getId(), Payment.Status.PENDING))
                .thenReturn(new ArrayList<>());
        Mockito.when(carRepository.save(car)).thenReturn(car);
        Mockito.when(rentalMapper.toModel(rentalRequestDto)).thenReturn(rental);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(rentalRepository.save(rental)).thenReturn(rental);
        Mockito.when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        RentalResponseDto actual = rentalService.addNewRental(user.getId(), rentalRequestDto);
        Assertions.assertEquals(rentalResponseDto.getId(), actual.getId());
        Assertions.assertEquals(rentalResponseDto.getRentalDate(), actual.getRentalDate());
        Assertions.assertEquals(rentalResponseDto.getCar().getId(), actual.getCar().getId());
        Assertions.assertEquals(rentalResponseDto.getReturnDate(), actual.getReturnDate());
    }

    @Test
    @DisplayName("User adding new rental having active rental. Throws exception")
    void addNewRental_UserHasActiveRental_ShouldReturnResponseDto() {
        Mockito.when(rentalRepository.findByUserIdAndActive(
                        Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(List.of(rental));
        Assertions.assertThrows(RentalException.class,
                () -> rentalService.addNewRental(1L, new CreateRentalRequestDto()));
    }

    @Test
    @DisplayName("No car with mentioned id in DB. Throws exception")
    void addNewRental_NoCarWithMentionedId_ShouldThrowException() {
        Mockito.when(rentalRepository.findByUserIdAndActive(user.getId(), true))
                .thenReturn(new ArrayList<>());
        Mockito.when(carRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> rentalService.addNewRental(user.getId(), rentalRequestDto));
    }

    @Test
    @DisplayName("User has a pending payment. Throws exception")
    void addNewRental_UserHasPendingPayment_ShouldThrowException() {
        Payment payment = new Payment();
        payment.setRental(rental);
        Mockito.when(rentalRepository.findByUserIdAndActive(user.getId(), true))
                .thenReturn(new ArrayList<>());
        Mockito.when(carRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(car));
        Mockito.when(paymentRepository.findByUserIdAndStatus(user.getId(), Payment.Status.PENDING))
                .thenReturn(List.of(payment));

        Assertions.assertThrows(RentalException.class,
                () -> rentalService.addNewRental(user.getId(), rentalRequestDto));
    }

    @Test
    @DisplayName("Rental date is before today's date. Throws exception")
    void addNewRental_WrongRentalDate_ShouldThrowException() {
        String newRentalDate = LocalDate.now()
                .minusDays(2)
                .format(FORMATTER);
        rentalRequestDto.setRentalDate(newRentalDate);

        Mockito.when(rentalRepository.findByUserIdAndActive(user.getId(), true))
                .thenReturn(new ArrayList<>());
        Mockito.when(carRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(car));
        Mockito.when(paymentRepository.findByUserIdAndStatus(user.getId(), Payment.Status.PENDING))
                .thenReturn(new ArrayList<>());

        Assertions.assertThrows(RentalException.class,
                () -> rentalService.addNewRental(user.getId(), rentalRequestDto));
    }

    @Test
    @DisplayName("No available cars. Car's inventory is 0. Throws exception")
    void addNewRental_NoAvailableCars_ShouldThrowException() {
        car.setInventory(0);
        Mockito.when(rentalRepository.findByUserIdAndActive(user.getId(), true))
                .thenReturn(new ArrayList<>());
        Mockito.when(carRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(car));
        Mockito.when(paymentRepository.findByUserIdAndStatus(user.getId(), Payment.Status.PENDING))
                .thenReturn(new ArrayList<>());

        Assertions.assertThrows(RentalException.class,
                () -> rentalService.addNewRental(user.getId(), rentalRequestDto));
    }

    @Test
    @DisplayName("Get rentals by user id and status")
    void getRentalsByParameters_ValidRequest_ShouldReturnListOfDtos() {
        rental.setActive(false);
        rental.setActualReturnDate(rental.getReturnDate());
        rentalResponseDto.setActualReturnDate(rentalResponseDto.getReturnDate());

        Mockito.when(rentalRepository.findByUserIdAndActive(user.getId(), false))
                .thenReturn(List.of(rental, rental2));
        Mockito.when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);
        Mockito.when(rentalMapper.toDto(rental2)).thenReturn(rentalResponseDto2);

        List<RentalResponseDto> expected = List.of(rentalResponseDto, rentalResponseDto2);
        List<RentalResponseDto> actual = rentalService.getRentalsByParameters(
                new RentalSearchParametersDto(user.getId(), false));

        Assertions.assertTrue(actual.containsAll(expected));
        Assertions.assertEquals(expected.get(0).getId(), actual.get(0).getId());
        Assertions.assertEquals(expected.get(1).getId(), actual.get(1).getId());
        Assertions.assertEquals(expected.get(0).getCar().getId(), actual.get(0).getCar().getId());
    }

    @Test
    @DisplayName("Get rentals by status")
    void getRentalsByStatus_ValidRequest_ShouldReturnListOfDtos() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());

        RentalResponseFullInfoDto responseFullInfoDto = new RentalResponseFullInfoDto();
        responseFullInfoDto.setId(rentalResponseDto.getId());
        responseFullInfoDto.setRentalDate(rentalResponseDto.getRentalDate());
        responseFullInfoDto.setReturnDate(rentalResponseDto.getReturnDate());
        responseFullInfoDto.setCar(carResponseDto);
        responseFullInfoDto.setUser(userResponseDto);

        RentalResponseFullInfoDto responseFullInfoDto2 = new RentalResponseFullInfoDto();
        responseFullInfoDto2.setId(rentalResponseDto2.getId());
        responseFullInfoDto2.setRentalDate(rentalResponseDto2.getRentalDate());
        responseFullInfoDto2.setReturnDate(rentalResponseDto2.getReturnDate());
        responseFullInfoDto2.setCar(carResponseDto);
        responseFullInfoDto2.setUser(userResponseDto);

        boolean isActive = false;
        Mockito.when(rentalRepository.findAllByActive(isActive))
                .thenReturn(List.of(rental, rental2));
        Mockito.when(rentalMapper.toFullInfoDto(rental)).thenReturn(responseFullInfoDto);
        Mockito.when(rentalMapper.toFullInfoDto(rental2)).thenReturn(responseFullInfoDto2);

        List<RentalResponseFullInfoDto> expected =
                List.of(responseFullInfoDto, responseFullInfoDto2);
        List<RentalResponseFullInfoDto> actual = rentalService.getRentalsByStatus(
                new RentalActiveStatusDto(isActive));

        Assertions.assertTrue(actual.containsAll(expected));
        Assertions.assertEquals(expected.get(0).getId(), actual.get(0).getId());
        Assertions.assertEquals(expected.get(1).getId(), actual.get(1).getId());
        Assertions.assertEquals(expected.get(0).getCar().getId(), actual.get(0).getCar().getId());
    }

    @Test
    @DisplayName("Get rental by id")
    void getRentalById_ValidRequest_ShouldReturnDto() {
        Mockito.when(rentalRepository.findByIdAndUserId(rental.getId(), user.getId()))
                .thenReturn(Optional.of(rental));
        Mockito.when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        RentalResponseDto actual = rentalService.getRentalById(user.getId(), rental.getId());

        Assertions.assertEquals(rentalResponseDto.getId(), actual.getId());
        Assertions.assertEquals(rentalResponseDto.getCar().getId(), actual.getCar().getId());
    }

    @Test
    @DisplayName("User tries to get rental that doesn't belong to him. Throws exception")
    void getRentalById_WrongRentalId_ThrowsException() {
        Mockito.when(rentalRepository.findByIdAndUserId(rental.getId(), user.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> rentalService.getRentalById(user.getId(), rental.getId()));
    }

    @Test
    @DisplayName("Return the rented car")
    void setActualReturnDate_ValidRequest_ShouldReturnDto() {
        Mockito.when(rentalRepository.findByIdAndUserId(rental.getId(), user.getId()))
                .thenReturn(Optional.of(rental));
        Mockito.when(paymentService.setPayment(new CreatePaymentRequestDto(
                        rental.getId(), Payment.Type.PAYMENT.name())))
                .thenReturn(new PaymentResponseDto());
        Mockito.when(rentalRepository.save(rental)).thenReturn(rental);
        Mockito.when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        RentalResponseDto actual = rentalService.setActualReturnDate(
                user.getId(), new ActualReturnDateDto(rental.getId()));

        Assertions.assertEquals(rentalResponseDto.getId(), actual.getId());
        Assertions.assertEquals(rentalResponseDto.getCar().getId(), actual.getCar().getId());
        Assertions.assertEquals(rentalResponseDto.getCar().getBrand(), actual.getCar().getBrand());
    }

    @Test
    @DisplayName("Return wrong rental. Throws exception")
    void setActualReturnDate_WrongRentalId_ShouldThrowException() {
        Mockito.when(rentalRepository.findByIdAndUserId(rental.getId(), user.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> rentalService.setActualReturnDate(
                        user.getId(), new ActualReturnDateDto(rental.getId())));
    }

    @Test
    @DisplayName("Returning previously returned rental. Throws exception")
    void setActualReturnDate_ReturnReturnedRental_ShouldThrowException() {
        rental.setActive(false);
        Mockito.when(rentalRepository.findByIdAndUserId(rental.getId(), user.getId()))
                .thenReturn(Optional.of(rental));

        Assertions.assertThrows(RuntimeException.class,
                () -> rentalService.setActualReturnDate(
                        user.getId(), new ActualReturnDateDto(rental.getId())));
    }
}
