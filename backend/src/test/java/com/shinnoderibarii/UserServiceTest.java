
package com.shinnoderibarii;

import com.shinnoderibarii.dto.UserRegisterRequest;
import com.shinnoderibarii.entity.User;
import com.shinnoderibarii.exception.EmailAlreadyExistsException;
import com.shinnoderibarii.mapper.UserMapper;
import com.shinnoderibarii.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userService = new UserService(userMapper);
    }

    @Test
    void register_shouldSaveUserWithHashedPassword() {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest(
                "Test User",
                "newuser@example.com",
                "TestPass123!");

        when(userMapper.findByEmail(request.email()))
                .thenReturn(null);

        // Act
        User result = userService.register(request);

        // Assert
        assertEquals("Test User", result.getName());
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("CUSTOMER", result.getRole());

        assertNotEquals(request.password(), result.getPassword());
        assertTrue(passwordEncoder.matches(
                request.password(),
                result.getPassword()));

        verify(userMapper).insert(any(User.class));
    }

    @Test
    void register_shouldRejectDuplicateEmail() {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest(
                "Test User",
                "existing@example.com",
                "TestPass123!");

        when(userMapper.findByEmail(request.email()))
                .thenReturn(new User());

        // Act & Assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.register(request));

        verify(userMapper, never()).insert(any(User.class));
    }
}
