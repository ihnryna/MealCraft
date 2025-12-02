package org.l5g7.mealcraft.authtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.l5g7.mealcraft.app.auth.security.JwtKey;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtKeyTest {

    @Test
    void constructor_WithValidBase64Secret_CreatesSecretKey() {

        String base64Secret = Base64.getEncoder().encodeToString(
                "this-is-a-very-long-secret-key-for-testing-purposes-with-sufficient-length".getBytes()
        );

        JwtKey jwtKey = new JwtKey(base64Secret);

        assertNotNull(jwtKey.getSecretKey());
    }

    @Test
    void constructor_WithPlainTextSecret_CreatesSecretKey() {
        String plainTextSecret = "this-is-a-very-long-secret-key-for-testing-purposes-with-sufficient-length";

        JwtKey jwtKey = new JwtKey(plainTextSecret);

        assertNotNull(jwtKey.getSecretKey());
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   "
    })
    void constructor_WithDifferentSecrets_ThrowsException(String secret) {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            new JwtKey(secret);
        });

        assertEquals("Property `jwt.secret` is not configured", exception.getMessage());
    }

    @Test
    void getSecretKey_ReturnsSameInstanceOnMultipleCalls() {
        String secret = "this-is-a-very-long-secret-key-for-testing-purposes-with-sufficient-length";
        JwtKey jwtKey = new JwtKey(secret);

        SecretKey key1 = jwtKey.getSecretKey();
        SecretKey key2 = jwtKey.getSecretKey();

        assertSame(key1, key2);
    }

    @Test
    void constructor_DifferentSecrets_CreateDifferentKeys() {
        String secret1 = "this-is-a-very-long-secret-key-for-testing-purposes-with-sufficient-length-1";
        String secret2 = "this-is-a-very-long-secret-key-for-testing-purposes-with-sufficient-length-2";

        JwtKey jwtKey1 = new JwtKey(secret1);
        JwtKey jwtKey2 = new JwtKey(secret2);

        assertNotEquals(jwtKey1.getSecretKey(), jwtKey2.getSecretKey());
    }

    @Test
    void constructor_SameSecret_CreatesSameKey() {
        String secret = "this-is-a-very-long-secret-key-for-testing-purposes-with-sufficient-length";

        JwtKey jwtKey1 = new JwtKey(secret);
        JwtKey jwtKey2 = new JwtKey(secret);

        assertEquals(jwtKey1.getSecretKey().getEncoded().length,
                jwtKey2.getSecretKey().getEncoded().length);
        assertArrayEquals(jwtKey1.getSecretKey().getEncoded(),
                jwtKey2.getSecretKey().getEncoded());
    }

    @Test
    void constructor_InvalidBase64_FallsBackToPlainText() {
        String invalidBase64 = "not-a-valid-base64-string-but-long-enough-for-hmac-sha-256-algorithm";

        assertDoesNotThrow(() -> {
            JwtKey jwtKey = new JwtKey(invalidBase64);
            assertNotNull(jwtKey.getSecretKey());
        });
    }

    @Test
    void getSecretKey_HasCorrectAlgorithm() {
        String secret = "this-is-a-very-long-secret-key-for-testing-purposes-with-sufficient-length";
        JwtKey jwtKey = new JwtKey(secret);

        SecretKey secretKey = jwtKey.getSecretKey();

        assertEquals("HmacSHA512", secretKey.getAlgorithm());
    }

    @Test
    void constructor_ShortSecret_StillCreatesKey() {
        String shortSecret = "this-is-a-secret-key-that-is-long-enough";

        assertDoesNotThrow(() -> {
            JwtKey jwtKey = new JwtKey(shortSecret);
            assertNotNull(jwtKey.getSecretKey());
        });
    }
}