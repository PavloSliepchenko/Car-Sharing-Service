package carsharing.carsharingservice.dto.rental;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRentalRequestDto {
    @NotNull
    private String rentalDate;
    @NotNull
    private String returnDate;
    @NotNull
    private Long carId;
}
