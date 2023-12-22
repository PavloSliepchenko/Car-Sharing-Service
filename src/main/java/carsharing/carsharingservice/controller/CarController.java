package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/cars")
@Tag(name = "Cars management", description = "End points for CRUD operations with cars")
public class CarController {
    private final CarService carService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Add a new car", description = "Saves a new car to DB")
    public CarResponseDto addCar(@Valid @RequestBody AddCarRequestDto requestDto) {
        return carService.saveCar(requestDto);
    }

    @GetMapping
    @Operation(summary = "Get all cars", description = "Returns all available cars by pages")
    public List<CarResponseDto> getAllCars(Pageable pageable) {
        return carService.getAllCars(pageable);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get a car by id", description = "Returns a car with certain id")
    public CarResponseDto getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Update a car info",
            description = "Updates all car's info with certain id")
    public CarResponseDto updateCar(@PathVariable Long id, @Valid AddCarRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Delete a car", description = "Soft deletes a car with certain id")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCarById(id);
    }
}
