package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.payment.CreatePaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.model.Payment;
import java.util.List;

public interface PaymentService {
    List<PaymentResponseDto> getPayments(Long userId);

    PaymentResponseDto setPayment(CreatePaymentRequestDto requestDto);

    PaymentResponseFullInfoDto updatePaymentStatus(String sessionId, Payment.Status status);
}
