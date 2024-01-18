package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import carsharing.carsharingservice.exception.EntityNotFoundException;
import carsharing.carsharingservice.exception.RegistrationException;
import carsharing.carsharingservice.mapper.UserMapper;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    private User user;
    private UserResponseDto userResponseDto;
    private UserRegistrationRequestDto registrationRequestDto;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(1L);
        user.setEmail("new@user.com");
        user.setLastName("User");
        user.setFirstName("New");
        user.setPassword("1234");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());

        registrationRequestDto = new UserRegistrationRequestDto();
        registrationRequestDto.setEmail(user.getEmail());
        registrationRequestDto.setPassword(user.getPassword());
        registrationRequestDto.setRepeatPassword(registrationRequestDto.getPassword());
        registrationRequestDto.setFirstName(user.getFirstName());
        registrationRequestDto.setLastName(user.getLastName());
    }

    @Test
    @DisplayName("Save a new user")
    void save_ValidRequest_ShouldReturnResponseDto() {
        Mockito.when(userRepository.findByEmail(registrationRequestDto.getEmail()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toDto(user)).thenReturn(userResponseDto);
        Mockito.when(userMapper.toModel(registrationRequestDto))
                .thenReturn(user);

        UserResponseDto actual = userService.save(registrationRequestDto);

        Assertions.assertEquals(userResponseDto.getId(), actual.getId());
        Assertions.assertEquals(userResponseDto.getEmail(), actual.getEmail());
        Assertions.assertEquals(userResponseDto.getFirstName(), actual.getFirstName());
    }

    @Test
    @DisplayName("Saving a user with the used email. Throws exception")
    void save_UsedWithUsedEmail_ShouldThrowException() {
        Mockito.when(userRepository.findByEmail(registrationRequestDto.getEmail()))
                .thenReturn(Optional.of(user));

        Assertions.assertThrows(RegistrationException.class,
                () -> userService.save(registrationRequestDto));
    }

    @Test
    @DisplayName("Update user's role")
    void updateRole_ValidRequest_ShouldReturnDto_ValidRequest_ShouldReturnDto() {
        String customerRole = User.Role.CUSTOMER.name();

        UserWithRoleResponseDto responseDto = new UserWithRoleResponseDto();
        responseDto.setRole(customerRole);
        responseDto.setId(userResponseDto.getId());
        responseDto.setEmail(userResponseDto.getEmail());
        responseDto.setLastName(userResponseDto.getLastName());
        responseDto.setFirstName(userResponseDto.getFirstName());

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDtoWithRole(user)).thenReturn(responseDto);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        UserWithRoleResponseDto actual = userService.updateRole(user.getId(),
                new RoleUpdateRequestDto(customerRole));

        Assertions.assertEquals(customerRole, actual.getRole());
        Assertions.assertEquals(responseDto.getEmail(), actual.getEmail());
        Assertions.assertEquals(responseDto.getFirstName(), actual.getFirstName());
    }

    @Test
    @DisplayName("Get user by id")
    void getById_ValidUserId_ShouldReturnResponseDto() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.getById(user.getId());

        Assertions.assertEquals(userResponseDto.getId(), actual.getId());
        Assertions.assertEquals(userResponseDto.getEmail(), actual.getEmail());
        Assertions.assertEquals(userResponseDto.getFirstName(), actual.getFirstName());
    }

    @Test
    @DisplayName("Get user by wrong id. Throws exception")
    void getById_WrongUserId_ShouldThrowException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getById(Mockito.anyLong()));
    }

    @Test
    @DisplayName("Update account information")
    void updateAccountInfo_ValidRequest_ShouldReturnResponseDto() {
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toDto(user)).thenReturn(userResponseDto);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserResponseDto actual = userService.updateAccountInfo(user.getId(),
                registrationRequestDto);

        Assertions.assertEquals(userResponseDto.getId(), actual.getId());
        Assertions.assertEquals(userResponseDto.getEmail(), actual.getEmail());
        Assertions.assertEquals(userResponseDto.getLastName(), actual.getLastName());
        Assertions.assertEquals(userResponseDto.getFirstName(), actual.getFirstName());
    }

    @Test
    @DisplayName("Setting already used email. Throws exception")
    void updateAccountInfo_SettingUsedEmail_ShouldThrowException() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setEmail("1234@user.com");
        registrationRequestDto.setEmail(anotherUser.getEmail());

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail(registrationRequestDto.getEmail()))
                .thenReturn(Optional.of(anotherUser));

        Assertions.assertThrows(RegistrationException.class,
                () -> userService.updateAccountInfo(user.getId(), registrationRequestDto));
    }
}
