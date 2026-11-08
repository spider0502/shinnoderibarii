
package com.shinnoderibarii.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder().encodeToString(
                "0123456789abcdef0123456789abcdef"
                        .getBytes(java.nio.charset.StandardCharsets.UTF_8));

        jwtService = new JwtService(secret, 3600000);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtService.generateToken("test@example.com");

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, "test@example.com"));
    }

    @Test
    void isTokenValid_shouldRejectDifferentEmail() {
        String token = jwtService.generateToken("test@example.com");

        assertFalse(jwtService.isTokenValid(token, "other@example.com"));
    }

    @Test
    void isTokenValid_shouldRejectInvalidToken() {
        assertFalse(
                jwtService.isTokenValid("invalid-token", "test@example.com"));
    }
}
