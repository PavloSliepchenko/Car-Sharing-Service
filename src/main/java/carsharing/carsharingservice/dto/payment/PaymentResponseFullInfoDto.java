package carsharing.carsharingservice.dto.payment;

import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentResponseFullInfoDto {
    private Long id;
    private String status;
    private String type;
    private String sessionId;
    private BigDecimal amount;
    private RentalResponseDto rental;
}
