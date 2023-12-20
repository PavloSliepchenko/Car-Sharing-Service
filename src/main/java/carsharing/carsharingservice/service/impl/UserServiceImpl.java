package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import carsharing.carsharingservice.exception.EntityNotFoundException;
import carsharing.carsharingservice.exception.RegistrationException;
import carsharing.carsharingservice.mapper.UserMapper;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final User.Role DEFAULT_ROLE = User.Role.CUSTOMER;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto save(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException(String.format("The user with email %s already exists",
                    requestDto.getEmail()));
        }
        User user = userMapper.toModel(requestDto);
        user.setRole(DEFAULT_ROLE);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserWithRoleResponseDto updateRole(Long userId, RoleUpdateRequestDto requestDto) {
        User user = getUserById(userId);
        user.setRole(User.Role.valueOf(requestDto.role()));
        return userMapper.toDtoWithRole(userRepository.save(user));
    }

    @Override
    public UserResponseDto getById(Long userId) {
        return userMapper.toDto(getUserById(userId));
    }

    @Override
    public UserResponseDto updateAccountInfo(Long userId, UserRegistrationRequestDto requestDto) {
        User user = getUserById(userId);
        Optional<User> emailCheckUserOptional = userRepository.findByEmail(requestDto.getEmail());
        if (emailCheckUserOptional.isPresent() && emailCheckUserOptional.get().getId() != userId) {
            throw new RegistrationException(String.format("User with email %s already exists",
                    requestDto.getEmail()));
        }
        user.setEmail(requestDto.getEmail());
        user.setLastName(requestDto.getLastName());
        user.setFirstName(requestDto.getFirstName());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("There is no user with id " + userId));
    }
}
