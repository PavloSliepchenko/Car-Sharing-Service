package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.exception.EntityNotFoundException;
import carsharing.carsharingservice.mapper.CarMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.service.CarService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public List<CarResponseDto> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .filter(e -> e.getInventory() > 0)
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarResponseDto saveCar(AddCarRequestDto requestDto) {
        Optional<Car> carOptional = carRepository.findByBrandAndModelAndType(
                requestDto.getBrand(),
                requestDto.getModel(),
                Car.CarType.valueOf(requestDto.getType())
        );
        if (carOptional.isPresent()) {
            throw new RuntimeException("This car has been added before. Car id "
                    + carOptional.get().getId());
        }
        return carMapper.toDto(carRepository.save(carMapper.toModel(requestDto)));
    }

    @Override
    public CarResponseDto getCarById(Long id) {
        return carMapper.toDto(getCarFromDbById(id));
    }

    @Override
    public CarResponseDto updateCar(Long id, AddCarRequestDto requestDto) {
        Car carFromDb = getCarFromDbById(id);
        carFromDb.setBrand(requestDto.getBrand());
        carFromDb.setType(Car.CarType.valueOf(requestDto.getType()));
        carFromDb.setInventory(requestDto.getInventory());
        carFromDb.setDailyFee(requestDto.getDailyFee());
        carFromDb.setModel(requestDto.getModel());
        return carMapper.toDto(carRepository.save(carFromDb));
    }

    @Override
    public void deleteCarById(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("No cars with id " + id);
        }
        carRepository.deleteById(id);
    }

    private Car getCarFromDbById(Long id) {
        Optional<Car> carFromDb = carRepository.findById(id);
        if (carFromDb.isEmpty()) {
            throw new EntityNotFoundException("No cars with id " + id);
        }
        return carFromDb.get();
    }
}
