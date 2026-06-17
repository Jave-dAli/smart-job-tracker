package com.javed.smartjobtracker.security;

import com.javed.smartjobtracker.user.entity.Role;
import com.javed.smartjobtracker.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "mysecretkeymysecretkeymysecretkey12"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                86400000L
        );

        user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldGenerateToken() {

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUserId() {

        String token = jwtService.generateToken(user);

        String userId = jwtService.extractUserId(token);

        assertEquals("1", userId);
    }

    @Test
    void shouldValidateToken() {

        String token = jwtService.generateToken(user);

        boolean valid =
                jwtService.isTokenValid(token, user);

        assertTrue(valid);
    }

    @Test
    void shouldRejectTokenForDifferentUser() {

        String token = jwtService.generateToken(user);

        User otherUser = User.builder()
                .id(999L)
                .email("other@gmail.com")
                .role(Role.USER)
                .build();

        boolean valid =
                jwtService.isTokenValid(
                        token,
                        otherUser
                );

        assertFalse(valid);
    }

    @Test
    void shouldContainEmailClaim() {

        String token = jwtService.generateToken(user);

        String email =
                jwtService.extractAllClaims(token)
                        .get("email", String.class);

        assertEquals(
                "test@gmail.com",
                email
        );
    }
}