package com.europace.userservice.service;

import com.europace.userservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "mySecretKey123456789mySecretKey123456789");
        ReflectionTestUtils.setField(jwtService, "expirationTime", 86400000L);
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(123L);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractUserId() {
        String token = jwtService.generateToken(123L);

        Long userId = jwtService.extractUserId(token);

        assertEquals(123L, userId);
    }

    @Test
    void shouldValidateValidToken() {
        String token = jwtService.generateToken(123L);

        boolean isValid = jwtService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void shouldRejectInvalidToken() {
        boolean isValid = jwtService.validateToken("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    void shouldRejectNullToken() {
        boolean isValid = jwtService.validateToken(null);

        assertFalse(isValid);
    }
}
