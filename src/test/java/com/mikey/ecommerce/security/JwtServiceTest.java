package com.mikey.ecommerce.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "test-jwt-secret-key-that-is-long-enough-for-hmac-signing-123456789";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 60_000L);
    }

    @Test
    void generateToken_shouldCreateTokenContainingUserEmailAndRole() {
        AppUser user = new AppUser(
                "Michael Westman",
                "michael@example.com",
                "encoded-password",
                Role.CUSTOMER
        );

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractEmail(token)).isEqualTo("michael@example.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("CUSTOMER");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void generateToken_shouldSupportAdminRole() {
        AppUser user = new AppUser(
                "Admin User",
                "admin@example.com",
                "encoded-password",
                Role.ADMIN
        );

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractEmail(token)).isEqualTo("admin@example.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void isTokenValid_shouldThrowExceptionWhenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1_000L);

        AppUser user = new AppUser(
                "Michael Westman",
                "michael@example.com",
                "encoded-password",
                Role.CUSTOMER
        );

        String expiredToken = jwtService.generateToken(user);

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void extractEmail_shouldThrowExceptionWhenTokenWasSignedWithDifferentSecret() {
        AppUser user = new AppUser(
                "Michael Westman",
                "michael@example.com",
                "encoded-password",
                Role.CUSTOMER
        );

        String token = jwtService.generateToken(user);

        JwtService otherJwtService = new JwtService();
        ReflectionTestUtils.setField(
                otherJwtService,
                "jwtSecret",
                "different-secret-key-that-is-long-enough-for-hmac-signing-123456789"
        );
        ReflectionTestUtils.setField(otherJwtService, "jwtExpirationMs", 60_000L);

        assertThatThrownBy(() -> otherJwtService.extractEmail(token))
                .isInstanceOf(RuntimeException.class);
    }
}