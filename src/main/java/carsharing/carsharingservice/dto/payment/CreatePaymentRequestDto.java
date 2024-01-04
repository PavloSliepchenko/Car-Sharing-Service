package carsharing.carsharingservice.dto.payment;

public record CreatePaymentRequestDto(Long rentalId, String paymentType) {
}
