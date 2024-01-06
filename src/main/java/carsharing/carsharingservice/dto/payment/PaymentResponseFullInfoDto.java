package carsharing.carsharingservice.dto.payment;

import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentResponseFullInfoDto {
    private Long id;
    private String type;
    private Long userId;
    private BigDecimal amount;
    private RentalResponseDto rental;
}
