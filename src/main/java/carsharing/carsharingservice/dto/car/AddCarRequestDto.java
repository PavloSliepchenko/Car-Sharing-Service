package carsharing.carsharingservice.dto.car;

import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AddCarRequestDto {
    @NotEmpty
    private String model;
    @NotEmpty
    private String brand;
    @NotEmpty
    private String type;
    @NotEmpty
    private int inventory;
    @NotEmpty
    private BigDecimal dailyFee;
}
