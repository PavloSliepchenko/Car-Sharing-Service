package carsharing.carsharingservice.dto.car;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AddCarRequestDto {
    @NotNull
    private String model;
    @NotNull
    private String brand;
    @NotNull
    private String type;
    @NotNull
    private int inventory;
    @NotNull
    @Min(value = 0)
    private BigDecimal dailyFee;
}
