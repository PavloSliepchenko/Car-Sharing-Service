package carsharing.carsharingservice.dto.rental;

import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import lombok.Data;

@Data
public class RentalResponseFullInfoDto {
    private Long id;
    private String rentalDate;
    private String returnDate;
    private String actualReturnDate;
    private CarResponseDto car;
    private UserResponseDto user;
}
