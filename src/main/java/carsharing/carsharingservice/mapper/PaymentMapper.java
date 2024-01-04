package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.model.Payment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = RentalMapper.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "rental.id")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "type", ignore = true)
    PaymentResponseDto toDto(Payment payment);

    PaymentResponseFullInfoDto toFullInfoDto(Payment payment);

    @AfterMapping
    default void setStatusAndType(@MappingTarget PaymentResponseDto responseDto, Payment payment) {
        responseDto.setType(payment.getType().name());
        responseDto.setStatus(payment.getStatus().name());
    }

    @AfterMapping
    default void setStatusAndType(@MappingTarget PaymentResponseFullInfoDto responseDto,
                                  Payment payment) {
        responseDto.setType(payment.getType().name());
        responseDto.setStatus(payment.getStatus().name());
    }
}
