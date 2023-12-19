package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    List<CarResponseDto> getAllCars(Pageable pageable);

    CarResponseDto saveCar(AddCarRequestDto requestDto);

    CarResponseDto getCarById(Long id);

    CarResponseDto updateCar(Long id, AddCarRequestDto requestDto);

    void deleteCarById(Long id);
}
