package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
@Tag(name = "Users management", description = "End points for CRUD operations with users")
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/me")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get personal user's info",
            description = "Returns user's info. The user must be authenticated to get his info")
    public UserResponseDto getUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getById(user.getId());
    }

    @PutMapping(value = "/{userId}/role")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Update user role",
            description = "Updates users role. Only manager can do it")
    public UserWithRoleResponseDto updateUserRole(@PathVariable Long userId,
                                                  @RequestBody RoleUpdateRequestDto requestDto) {
        return userService.updateRole(userId, requestDto);
    }

    @PatchMapping(value = "/me")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Update profile info",
            description = "Using this endpoint an authenticated user can update his profile info")
    public UserResponseDto updateProfileInfo(Authentication authentication,
                                             @RequestBody UserRegistrationRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateAccountInfo(user.getId(), requestDto);
    }
}
