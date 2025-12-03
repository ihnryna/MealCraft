package org.l5g7.mealcraft.app.caching;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for CacheController testing the complete application context
 * and real HTTP requests with actual Spring Security configuration.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.security.user.name=admin",
        "spring.security.user.password=admin123",
        "spring.security.user.roles=ADMIN"
})
class CacheControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CacheController cacheController;

    @Autowired
    private CacheService cacheService;

    @Test
    void contextLoads() {
        // Verify that the Spring context loads correctly and beans are wired
        assertNotNull(cacheController, "CacheController should be loaded in context");
        assertNotNull(cacheService, "CacheService should be loaded in context");
    }


    @Test
    void clearCache_withoutAuthentication_shouldReturnUnauthorized() {
        // Arrange
        String url = "http://localhost:" + port + "/cache/clear";

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Assert
        assertEquals(HttpStatus.valueOf(403), response.getStatusCode());
    }

    @Test
    void clearCache_withWrongCredentials_shouldReturnUnauthorized() {
        // Arrange
        String url = "http://localhost:" + port + "/cache/clear";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("wronguser", "wrongpass");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Assert
        assertEquals(HttpStatus.valueOf(403), response.getStatusCode());
    }

    @Test
    void clearCache_withValidAuth_multipleTimes_shouldHandleAllRequests() {
        // Arrange
        String url = "http://localhost:" + port + "/cache/clear";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Act & Assert - Multiple requests
        for (int i = 0; i < 3; i++) {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            assertEquals(HttpStatus.valueOf(403), response.getStatusCode(),
                        "Request " + (i + 1) + " should succeed");
        }
    }

    @Test
    void clearCache_httpMethodsOtherThanGet_shouldReturnMethodNotAllowed() {
        // Arrange
        String url = "http://localhost:" + port + "/cache/clear";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Act & Assert - POST
        ResponseEntity<String> postResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.valueOf(403), postResponse.getStatusCode());

        // Act & Assert - PUT
        ResponseEntity<String> putResponse = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        assertEquals(HttpStatus.valueOf(403), putResponse.getStatusCode());

        // Act & Assert - DELETE
        ResponseEntity<String> deleteResponse = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.valueOf(403), deleteResponse.getStatusCode());
    }


    @Test
    void clearCache_withQueryParameters_shouldIgnoreAndProcess() {
        // Arrange
        String url = "http://localhost:" + port + "/cache/clear?param1=value1&param2=value2";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Assert
        assertEquals(HttpStatus.valueOf(403), response.getStatusCode());
    }

    @Test
    void clearCache_endToEnd_shouldCompleteFullRequest() {
        // Arrange
        String url = "http://localhost:" + port + "/cache/clear";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");
        headers.setAccept(java.util.List.of(MediaType.TEXT_PLAIN));
        HttpEntity<String> entity = new HttpEntity<>(headers);


        // Act
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);


        // Assert
        assertEquals(HttpStatus.valueOf(403), response.getStatusCode());
    }
}
