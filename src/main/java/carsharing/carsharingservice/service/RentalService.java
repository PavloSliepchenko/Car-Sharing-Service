package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.rental.ActualReturnDateDto;
import carsharing.carsharingservice.dto.rental.CreateRentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalActiveStatusDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalResponseFullInfoDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import java.util.List;

public interface RentalService {
    RentalResponseDto addNewRental(Long userId, CreateRentalRequestDto requestDto);

    List<RentalResponseDto> getRentalsByParameters(RentalSearchParametersDto parametersDto);

    List<RentalResponseFullInfoDto> getRentalsByStatus(RentalActiveStatusDto requestDto);

    RentalResponseDto getRentalById(Long userId, Long rentalId);

    RentalResponseDto setActualReturnDate(Long userId, ActualReturnDateDto returnDateDto);
}
