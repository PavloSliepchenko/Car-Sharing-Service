package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalResponseFullInfoDto;
import java.util.List;

public interface NotificationService {
    void sendNotification(Long userId, RentalResponseDto responseDto);

    void sendNotification(List<RentalResponseFullInfoDto> responseDto);
}
