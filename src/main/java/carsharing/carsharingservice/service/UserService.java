package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;

public interface UserService {
    UserResponseDto save(UserRegistrationRequestDto requestDto);

    UserWithRoleResponseDto updateRole(Long userId, RoleUpdateRequestDto requestDto);

    UserResponseDto getById(Long userId);

    UserResponseDto updateAccountInfo(Long userId, UserRegistrationRequestDto requestDto);
}
