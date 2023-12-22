package carsharing.carsharingservice.dto.rental;

import carsharing.carsharingservice.dto.car.CarResponseDto;
import lombok.Data;

@Data
public class RentalResponseDto {
    private Long id;
    private String rentalDate;
    private String returnDate;
    private String actualReturnDate;
    private CarResponseDto car;
}
