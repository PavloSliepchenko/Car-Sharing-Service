package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.exception.EntityNotFoundException;
import carsharing.carsharingservice.mapper.CarMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.service.impl.CarServiceImpl;
import java.math.BigDecimal;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    private Car car1;
    private Car car2;
    private Car car3;
    private CarResponseDto car1Dto;
    private CarResponseDto car2Dto;
    private CarResponseDto car3Dto;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    private void initCars() {
        car1 = new Car();
        car1.setId(1L);
        car1.setBrand("BMW");
        car1.setType(Car.CarType.SUV);
        car1.setModel("X5");
        car1.setInventory(4);
        car1.setDailyFee(BigDecimal.valueOf(32));

        car2 = new Car();
        car2.setId(2L);
        car2.setBrand("BMW");
        car2.setType(Car.CarType.SEDAN);
        car2.setModel("M5");
        car2.setInventory(7);
        car2.setDailyFee(BigDecimal.valueOf(50));

        car3 = new Car();
        car3.setId(3L);
        car3.setBrand("SKODA");
        car3.setType(Car.CarType.HATCHBACK);
        car3.setModel("OCTAVIA");
        car3.setInventory(12);
        car3.setDailyFee(BigDecimal.valueOf(21));

        car1Dto = new CarResponseDto();
        car1Dto.setId(car1.getId());
        car1Dto.setBrand(car1.getBrand());
        car1Dto.setType(car1.getType().name());
        car1Dto.setModel(car1.getModel());
        car1Dto.setDailyFee(car1.getDailyFee());

        car2Dto = new CarResponseDto();
        car2Dto.setId(car2.getId());
        car2Dto.setBrand(car2.getBrand());
        car2Dto.setType(car2.getType().name());
        car2Dto.setModel(car2.getModel());
        car2Dto.setDailyFee(car2.getDailyFee());

        car3Dto = new CarResponseDto();
        car3Dto.setId(car3.getId());
        car3Dto.setBrand(car3.getBrand());
        car3Dto.setType(car3.getType().name());
        car3Dto.setModel(car3.getModel());
        car3Dto.setDailyFee(car3.getDailyFee());
    }

    @Test
    @DisplayName("Get all cars")
    void getAllCars_ValidRequest_ShouldReturnListOfDtos() {
        List<Car> cars = List.of(car1, car2, car3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> bookPage = new PageImpl<>(cars, pageable, cars.size());
        Mockito.when(carRepository.findAll(pageable)).thenReturn(bookPage);
        Mockito.when(carMapper.toDto(car1)).thenReturn(car1Dto);
        Mockito.when(carMapper.toDto(car2)).thenReturn(car2Dto);
        Mockito.when(carMapper.toDto(car3)).thenReturn(car3Dto);

        List<CarResponseDto> expected = List.of(car1Dto, car2Dto, car3Dto);
        List<CarResponseDto> actual = carService.getAllCars(pageable);

        Assertions.assertTrue(actual.containsAll(expected));
        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("Save a new car")
    void saveCar_ValidSaveCarRequestDto_ShouldReceiveResponseDto() {
        AddCarRequestDto carToSave = new AddCarRequestDto();
        carToSave.setInventory(car1.getInventory());
        carToSave.setBrand(car1.getBrand());
        carToSave.setType(car1.getType().name());
        carToSave.setModel(car1.getModel());
        carToSave.setDailyFee(car1.getDailyFee());

        Car carToSaveModel = new Car();
        carToSaveModel.setInventory(car1.getInventory());
        carToSaveModel.setBrand(car1.getBrand());
        carToSaveModel.setType(car1.getType());
        carToSaveModel.setModel(car1.getModel());
        carToSaveModel.setDailyFee(car1.getDailyFee());

        Mockito.when(carRepository.save(carToSaveModel)).thenReturn(car1);
        Mockito.when(carMapper.toModel(carToSave)).thenReturn(carToSaveModel);
        Mockito.when(carMapper.toDto(car1)).thenReturn(car1Dto);
        CarResponseDto actual = carService.saveCar(carToSave);

        Assertions.assertEquals(car1.getId(), actual.getId());
        Assertions.assertEquals(car1.getBrand(), actual.getBrand());
        Assertions.assertEquals(car1.getModel(), actual.getModel());
    }

    @Test
    @DisplayName("Get car by id")
    void getCarById_ValidCarId_ShouldReturnCarDto() {
        Mockito.when(carRepository.findById(1L)).thenReturn(Optional.of(car1));
        Mockito.when(carMapper.toDto(car1)).thenReturn(car1Dto);
        CarResponseDto actual = carService.getCarById(1L);
        Assertions.assertEquals(car1Dto.getId(), actual.getId());
        Assertions.assertEquals(car1Dto.getType(), actual.getType());
        Assertions.assertEquals(car1Dto.getModel(), actual.getModel());
        Assertions.assertEquals(car1Dto.getDailyFee(), actual.getDailyFee());

        Mockito.when(carRepository.findById(3L)).thenReturn(Optional.of(car3));
        Mockito.when(carMapper.toDto(car3)).thenReturn(car3Dto);
        actual = carService.getCarById(3L);
        Assertions.assertEquals(car3Dto.getId(), actual.getId());
        Assertions.assertEquals(car3Dto.getType(), actual.getType());
        Assertions.assertEquals(car3Dto.getModel(), actual.getModel());
        Assertions.assertEquals(car3Dto.getDailyFee(), actual.getDailyFee());
    }

    @Test
    @DisplayName("Update car")
    void updateCar() {
        AddCarRequestDto updateRequest = new AddCarRequestDto();
        updateRequest.setBrand(car3.getBrand());
        updateRequest.setType(car3.getType().name());
        updateRequest.setModel(car3.getModel());
        updateRequest.setInventory(car3.getInventory());
        updateRequest.setDailyFee(car3.getDailyFee());
        Long carId = 2L;
        car3.setId(carId);
        car3Dto.setId(carId);
        Mockito.when(carRepository.save(car3)).thenReturn(car3);
        Mockito.when(carMapper.toDto(car3)).thenReturn(car3Dto);
        Mockito.when(carRepository.findById(carId)).thenReturn(Optional.of(car2));
        CarResponseDto actual = carService.updateCar(carId, updateRequest);
        Assertions.assertEquals(car3Dto.getId(), actual.getId());
        Assertions.assertEquals(car3Dto.getBrand(), actual.getBrand());
        Assertions.assertEquals(car3Dto.getType(), actual.getType());
    }

    @Test
    @DisplayName("Delete car by wrong id. Throws exception")
    void deleteCarById_WrongId_ShouldThrowException() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> carService.deleteCarById(Mockito.anyLong()));
    }

    @Test
    @DisplayName("Update car with wrong id. Throws exception")
    void updateCar_WrongId_ShouldThrowException() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> carService.updateCar(Mockito.anyLong(), new AddCarRequestDto()));
    }
}
