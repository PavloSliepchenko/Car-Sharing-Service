package carsharing.carsharingservice.dto.rental;

import jakarta.validation.constraints.Positive;

public record ActualReturnDateDto(@Positive Long rentalId) {
}
