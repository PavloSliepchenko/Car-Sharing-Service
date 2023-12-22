package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import carsharing.carsharingservice.dto.rental.CreateRentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalResponseFullInfoDto;
import carsharing.carsharingservice.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CarMapper.class, UserMapper.class})
public interface RentalMapper {
    @Mapping(target = "rentalDate", source = "rentalDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "returnDate", source = "returnDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "car", source = "carId", qualifiedByName = "GetCarById")
    Rental toModel(CreateRentalRequestDto requestDto);

    @Mapping(target = "rentalDate", source = "rentalDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "returnDate", source = "returnDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "actualReturnDate", source = "actualReturnDate", dateFormat = "dd/MM/yyyy")
    RentalResponseDto toDto(Rental rental);

    @Mapping(target = "rentalDate", source = "rentalDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "returnDate", source = "returnDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "actualReturnDate", source = "actualReturnDate", dateFormat = "dd/MM/yyyy")
    RentalResponseFullInfoDto toFullInfoDto(Rental rental);
}
