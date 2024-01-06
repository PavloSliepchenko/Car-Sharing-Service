package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalResponseFullInfoDto;
import carsharing.carsharingservice.service.NotificationService;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements NotificationService {
    private static final Dotenv DOTENV = Dotenv.configure().load();
    private static final String NO_OVERDUE_RENTS = "No rentals overdue today!";
    private static final String TELEGRAM_CHAT_ID = DOTENV.get("TELEGRAM_CHAT_ID");
    private static final String TELEGRAM_BOT_TOKEN = DOTENV.get("TELEGRAM_BOT_TOKEN");
    private static final String API_URL = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN
            + "/sendMessage";

    @Override
    public void sendNotification(Long userId, RentalResponseDto responseDto) {
        String message = String.format("""
                        New rental!
                        User id: %s
                        Rental id: %s
                        Rental date: %s
                        Return date: %s
                        Info about rented car:
                        Car id: %s
                        Car model: %s
                        Car brand: %s
                        Daily fee: %s
                        """, userId, responseDto.getId(), responseDto.getRentalDate(),
                responseDto.getReturnDate(), responseDto.getCar().getId(),
                responseDto.getCar().getModel(), responseDto.getCar().getBrand(),
                responseDto.getCar().getDailyFee());
        sendMessage(message);
    }

    @Override
    public void sendNotification(List<RentalResponseFullInfoDto> responseDto) {
        if (responseDto.size() == 0) {
            sendMessage(NO_OVERDUE_RENTS);
        }

        for (RentalResponseFullInfoDto overdueRent : responseDto) {
            String message = String.format("""
                            Overdue rental!
                            Rental id: %s
                            User id: %s
                            Supposed return date: %s
                            Info about rented car:
                            Car id: %s
                            Car model: %s
                            Car brand: %s
                            """, overdueRent.getId(), overdueRent.getUser().getId(),
                    overdueRent.getReturnDate(), overdueRent.getCar().getId(),
                    overdueRent.getCar().getModel(), overdueRent.getCar().getBrand());
            sendMessage(message);
        }
    }

    @Override
    public void sendNotification(PaymentResponseFullInfoDto responseDto) {
        String message = String.format("""
                        New payment!
                        User id: %s
                        Rental id: %s
                        Car id: %s
                        Amount: $%s
                        Payment type: %s
                        """, responseDto.getUserId(), responseDto.getRental().getId(),
                responseDto.getRental().getCar().getId(),
                responseDto.getAmount(), responseDto.getType());
        sendMessage(message);
    }

    private void sendMessage(String message) {
        String requestUrl = String.format("%s?chat_id=%s&text=%s",
                API_URL, TELEGRAM_CHAT_ID, message);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(requestUrl, null, String.class);
    }
}
