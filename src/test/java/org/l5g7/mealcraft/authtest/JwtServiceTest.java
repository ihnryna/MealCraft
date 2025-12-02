package org.l5g7.mealcraft.authtest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.auth.security.JwtKey;
import org.l5g7.mealcraft.app.auth.security.JwtService;
import org.l5g7.mealcraft.enums.Role;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtKey jwtKey;

    private JwtService jwtService;
    private SecretKey testSecretKey;

    private static final String TEST_USERNAME = "testuser";
    private static final Role TEST_ROLE = Role.USER;
    private static final long TEST_EXPIRATION_MS = 86400000L; // 1 day

    @BeforeEach
    void setUp() {
        testSecretKey = Jwts.SIG.HS256.key().build();

        when(jwtKey.getSecretKey()).thenReturn(testSecretKey);

        jwtService = new JwtService(jwtKey);
        ReflectionTestUtils.setField(jwtService, "expirationMs", TEST_EXPIRATION_MS);
    }

    @Test
    void generateToken_Success_ReturnsValidToken() {
        String token = jwtService.generateToken(TEST_USERNAME, TEST_ROLE);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts");
    }

    @Test
    void generateToken_ContainsCorrectClaims() {
        String token = jwtService.generateToken(TEST_USERNAME, TEST_ROLE);

        Claims claims = Jwts.parser()
                .verifyWith(testSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(TEST_USERNAME, claims.getSubject());
        assertEquals(TEST_ROLE.toString(), claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateToken_HasCorrectExpiration() {
        long beforeGeneration = System.currentTimeMillis();

        String token = jwtService.generateToken(TEST_USERNAME, TEST_ROLE);

        Claims claims = Jwts.parser()
                .verifyWith(testSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long expectedExpiration = beforeGeneration + TEST_EXPIRATION_MS;
        long actualExpiration = claims.getExpiration().getTime();

        assertTrue(Math.abs(actualExpiration - expectedExpiration) < 1000);
    }

    @Test
    void getUsernameFromToken_ValidToken_ReturnsUsername() {
        String token = jwtService.generateToken(TEST_USERNAME, TEST_ROLE);

        String username = jwtService.getUsernameFromToken(token);

        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void getUsernameFromToken_ExpiredToken_ReturnsNull() {
        String expiredToken = Jwts.builder()
                .subject(TEST_USERNAME)
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(testSecretKey)
                .compact();

        String username = jwtService.getUsernameFromToken(expiredToken);

        assertNull(username);
    }

    @Test
    void getRolesFromToken_ValidToken_ReturnsRole() {
        String token = jwtService.generateToken(TEST_USERNAME, TEST_ROLE);

        String role = jwtService.getRolesFromToken(token);

        assertEquals(TEST_ROLE.toString(), role);
    }

    @Test
    void getRolesFromToken_ExpiredToken_ReturnsNull() {
        String expiredToken = Jwts.builder()
                .claim("role", TEST_ROLE.toString())
                .subject(TEST_USERNAME)
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(testSecretKey)
                .compact();

        String role = jwtService.getRolesFromToken(expiredToken);

        assertNull(role);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(TEST_USERNAME, TEST_ROLE);

        boolean isValid = jwtService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        String expiredToken = Jwts.builder()
                .subject(TEST_USERNAME)
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(testSecretKey)
                .compact();

        boolean isValid = jwtService.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid-jwt-token";

        boolean isValid = jwtService.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void generateToken_DifferentRoles_CreatesDistinctTokens() {
        String userToken = jwtService.generateToken(TEST_USERNAME, Role.USER);
        String adminToken = jwtService.generateToken(TEST_USERNAME, Role.ADMIN);

        assertNotEquals(userToken, adminToken);

        String userRole = jwtService.getRolesFromToken(userToken);
        String adminRole = jwtService.getRolesFromToken(adminToken);

        assertEquals(Role.USER.toString(), userRole);
        assertEquals(Role.ADMIN.toString(), adminRole);
    }

    @Test
    void generateToken_DifferentUsernames_CreatesDistinctTokens() {
        String token1 = jwtService.generateToken("user1", TEST_ROLE);
        String token2 = jwtService.generateToken("user2", TEST_ROLE);

        assertNotEquals(token1, token2);

        String username1 = jwtService.getUsernameFromToken(token1);
        String username2 = jwtService.getUsernameFromToken(token2);

        assertEquals("user1", username1);
        assertEquals("user2", username2);
    }

    @Test
    void tokenRoundTrip_PreservesAllData() {
        String username = "testuser";
        Role role = Role.ADMIN;

        String token = jwtService.generateToken(username, role);
        String extractedUsername = jwtService.getUsernameFromToken(token);
        String extractedRole = jwtService.getRolesFromToken(token);
        boolean isValid = jwtService.validateToken(token);

        assertEquals(username, extractedUsername);
        assertEquals(role.toString(), extractedRole);
        assertTrue(isValid);
    }
}