package com.javed.smartjobtracker.auth.service;

import com.javed.smartjobtracker.auth.dto.AuthResponse;
import com.javed.smartjobtracker.auth.dto.LoginRequest;
import com.javed.smartjobtracker.auth.dto.RegisterRequest;
import com.javed.smartjobtracker.security.JwtService;
import com.javed.smartjobtracker.user.entity.Role;
import com.javed.smartjobtracker.user.entity.User;
import com.javed.smartjobtracker.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterSuccessfully() {

        RegisterRequest request =
                new RegisterRequest();

        request.setFirstName("Ali");
        request.setLastName("Raza");
        request.setEmail("ali@gmail.com");
        request.setPassword("P@ssword1");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(any()))
                .thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .email("ali@gmail.com")
                .role(Role.USER)
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("jwt-token");

        AuthResponse response =
                authService.register(request);

        assertNotNull(response);

        assertEquals(
                "jwt-token",
                response.getToken()
        );

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {

        RegisterRequest request =
                new RegisterRequest();

        request.setEmail("ali@gmail.com");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Email already exists",
                ex.getMessage()
        );
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("ali@gmail.com");
        request.setPassword("P@ssword1");

        User user = User.builder()
                .id(1L)
                .email("ali@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(
                request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        AuthResponse response =
                authService.login(request);

        assertEquals(
                "jwt-token",
                response.getToken()
        );

        assertEquals(
                "ali@gmail.com",
                response.getEmail()
        );
    }

    @Test
    void shouldRejectUnknownEmail() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("unknown@gmail.com");
        request.setPassword("password");

        when(userRepository.findByEmail(
                request.getEmail()))
                .thenReturn(Optional.empty());

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid credentials",
                ex.getMessage()
        );
    }

    @Test
    void shouldRejectWrongPassword() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("ali@gmail.com");
        request.setPassword("wrong");

        User user = User.builder()
                .email("ali@gmail.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(
                request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
                .thenReturn(false);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid credentials",
                ex.getMessage()
        );
    }
}