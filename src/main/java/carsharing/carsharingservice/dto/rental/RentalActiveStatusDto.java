package carsharing.carsharingservice.dto.rental;

import jakarta.validation.constraints.NotNull;

public record RentalActiveStatusDto(@NotNull boolean isActive) {
}
