package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.payment.CreatePaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.exception.PaymentException;
import carsharing.carsharingservice.mapper.PaymentMapper;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String SUCCESS = "Success";
    private static final String SUCCESS_URL =
            "http://localhost:8081/payments/success?session_id={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL =
            "http://localhost:8081/payments/cancel?session_id={CHECKOUT_SESSION_ID}";
    private static final String STIPE_SECRET_KEY = Dotenv.load().get("STRIPE_SECRET_KEY");
    private static final Payment.Status DEFAULT_PAYMENT_STATUS = Payment.Status.PENDING;
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final Payment.Type PAYMENT = Payment.Type.PAYMENT;
    private static final Payment.Type FINE = Payment.Type.FINE;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentResponseDto> getPayments(Long userId) {
        return paymentRepository.getPaymentsByUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponseDto setPayment(CreatePaymentRequestDto requestDto) {
        Rental rental = getRentalById(requestDto.rentalId());
        Payment.Type typeOfPayment = Payment.Type.valueOf(requestDto.paymentType());
        Optional<Payment> paymentOptional = paymentRepository.findByRentalIdAndType(
                requestDto.rentalId(), typeOfPayment).stream()
                .findFirst();

        if (paymentOptional.isEmpty()) {
            BigDecimal numberOfDayRent = typeOfPayment == PAYMENT
                    ? BigDecimal.valueOf(getNumberOfDaysRent(rental))
                    : BigDecimal.valueOf(getNumberOfFineDays(rental));

            BigDecimal amountToPay = typeOfPayment == PAYMENT
                    ? rental.getCar().getDailyFee().multiply(numberOfDayRent)
                    : rental.getCar().getDailyFee().multiply(numberOfDayRent)
                            .multiply(FINE_MULTIPLIER);

            Session session = createSession(amountToPay, typeOfPayment);
            String description = null;
            URI checkoutUri = null;
            try {
                checkoutUri = new URI(session.getUrl());
                description = openUrlInBrowser(checkoutUri);
            } catch (Exception e) {
                description = "You can finish the payment following the link: " + checkoutUri;
            }

            Payment payment = new Payment();
            payment.setRental(rental);
            payment.setType(typeOfPayment == PAYMENT ? PAYMENT : FINE);
            payment.setAmount(amountToPay);
            payment.setSessionId(session.getId());
            payment.setStatus(DEFAULT_PAYMENT_STATUS);
            try {
                payment.setSession(new URL(session.getUrl()));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            PaymentResponseDto responseDto = paymentMapper.toDto(paymentRepository.save(payment));
            responseDto.setDescription(description);
            return responseDto;
        } else if (paymentOptional.get().getSessionId() != null
                && paymentOptional.get().getStatus() == DEFAULT_PAYMENT_STATUS) {
            Stripe.apiKey = STIPE_SECRET_KEY;
            try {
                Session session = Session.retrieve(paymentOptional.get().getSessionId());
                String description = openUrlInBrowser(new URI(session.getUrl()));
                PaymentResponseDto responseDto = paymentMapper.toDto(paymentOptional.get());
                responseDto.setDescription(description);
                return responseDto;
            } catch (StripeException | URISyntaxException e) {
                throw new PaymentException("Failed to retrieve session", e);
            }
        }
        throw new PaymentException("This rental has been paid before");
    }

    @Override
    public PaymentResponseFullInfoDto updatePaymentStatus(String sessionId, Payment.Status status) {
        Optional<Payment> paymentOptional = paymentRepository.findBySessionId(sessionId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus(status);
            return paymentMapper.toFullInfoDto(paymentRepository.save(payment));
        }
        throw new PaymentException("No payments were found with session id " + sessionId);
    }

    private Rental getRentalById(Long rentalId) {
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);
        if (rentalOptional.isEmpty()) {
            throw new PaymentException("There is no rental with id " + rentalId);
        }
        return rentalOptional.get();
    }

    private int getNumberOfDaysRent(Rental rental) {
        if (rental.getRentalDate().isEqual(rental.getActualReturnDate())) {
            return 1;
        }
        if (rental.getRentalDate().getYear() - rental.getActualReturnDate().getYear() < 0) {
            LocalDate dec31 = LocalDate.of(rental.getRentalDate().getYear(), Month.DECEMBER, 31);
            int daysUsedPreviousYear = dec31.getDayOfYear() - rental.getRentalDate().getDayOfYear();
            return daysUsedPreviousYear + rental.getActualReturnDate().getDayOfYear();
        }
        return rental.getActualReturnDate().getDayOfYear() - rental.getRentalDate().getDayOfYear();
    }

    private int getNumberOfFineDays(Rental rental) {
        if (rental.getReturnDate().getYear() - rental.getActualReturnDate().getYear() < 0) {
            LocalDate dec31 = LocalDate.of(rental.getReturnDate().getYear(), Month.DECEMBER, 31);
            int daysUsedPreviousYear = dec31.getDayOfYear() - rental.getReturnDate().getDayOfYear();
            return daysUsedPreviousYear + rental.getActualReturnDate().getDayOfYear();
        }
        return rental.getActualReturnDate().getDayOfYear() - rental.getReturnDate().getDayOfYear();
    }

    private Session createSession(BigDecimal amountToPay, Payment.Type type) {
        Stripe.apiKey = STIPE_SECRET_KEY;
        SessionCreateParams.Builder sessionBuilder = new SessionCreateParams.Builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addLineItem(
                        SessionCreateParams
                                .LineItem
                                .builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmountDecimal(amountToPay
                                                        .multiply(BigDecimal.valueOf(100)))
                                                .setProductData(SessionCreateParams
                                                        .LineItem
                                                        .PriceData
                                                        .ProductData
                                                        .builder()
                                                        .setName("Car Rental (" + type + ")")
                                                        .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                );
        SessionCreateParams sessionParams = sessionBuilder.build();
        try {
            return Session.create(sessionParams);
        } catch (StripeException e) {
            throw new PaymentException("Failed to create a session", e);
        }
    }

    private String openUrlInBrowser(URI uri) {
        String[] browsers = {"google-chrome", "firefox", "opera", "epiphany",
                "konqueror", "mozilla", "netscape", "xdg-open"};
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime runtime = Runtime.getRuntime();
            if (os.contains("win")) {
                runtime.exec("rundll32 url.dll,FileProtocolHandler " + uri.toString());
                return SUCCESS;
            } else if (os.contains("mac")) {
                runtime.exec("open " + uri.toString());
                return SUCCESS;
            } else if (os.contains("nix") || os.contains("nux")) {
                for (String browser : browsers) {
                    if (runtime.exec(new String[]{browser, uri.toString()}) == null) {
                        continue;
                    }
                    break;
                }
                return SUCCESS;
            } else {
                return "You can finish the payment following the link: " + uri;
            }

        } catch (Exception e) {
            return "You can finish the payment following the link: " + uri;
        }
    }
}
