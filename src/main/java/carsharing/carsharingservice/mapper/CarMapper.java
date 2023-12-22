package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.model.Car;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    @Mapping(target = "type", ignore = true)
    Car toModel(AddCarRequestDto requestDto);

    @AfterMapping
    default void setCarType(@MappingTarget Car car, AddCarRequestDto requestDto) {
        car.setType(Car.CarType.valueOf(requestDto.getType()));
    }

    CarResponseDto toDto(Car car);

    @Named("GetCarById")
    default Car getCarById(Long carId) {
        Car car = new Car();
        car.setId(carId);
        return car;
    }
}
