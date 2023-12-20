package carsharing.carsharingservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Length(min = 5, max = 15)
        String password
) {
}
