package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.payment.CreatePaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.service.NotificationService;
import carsharing.carsharingservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/payments")
@Tag(name = "Payments management", description = "End points for CRUD operations with payments")
public class PaymentController {
    private final NotificationService notificationService;
    private final PaymentService paymentService;

    @GetMapping(value = "/{userId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get payment", description = "Get all payments of a certain user")
    public List<PaymentResponseDto> getPayments(@PathVariable Long userId) {
        return paymentService.getPayments(userId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "To pay", description = "Set payment for certain rental")
    public PaymentResponseDto setPayment(@RequestBody CreatePaymentRequestDto requestDto) {
        return paymentService.setPayment(requestDto);
    }

    @GetMapping(value = "/success")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Success endpoint", description = "Endpoint for redirection in case of "
            + "successful payment operation. Updates payment status to 'PAID' "
            + "and sends a notification to telegram chat")
    public String success(@RequestParam("session_id") String sessionId) {
        PaymentResponseFullInfoDto responseDto =
                paymentService.updatePaymentStatus(sessionId, Payment.Status.PAID);
        notificationService.sendNotification(responseDto);
        return "success";
    }

    @GetMapping(value = "/cancel")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Cancel endpoint", description = "Endpoint for redirection in case of "
            + "payment cancellation")
    public String cancel(@RequestParam("session_id") String sessionId) {
        return "canceled";
    }
}
