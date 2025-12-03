package org.l5g7.mealcraft.app.caching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CacheController focusing on business logic and service integration.
 */
@ExtendWith(MockitoExtension.class)
class CacheControllerTest {

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CacheController cacheController;

    @BeforeEach
    void setUp() {
        // Any additional setup if needed
    }

    @Test
    void constructor_withValidCacheService_shouldInitializeCorrectly() {
        // Arrange
        CacheService service = mock(CacheService.class);

        // Act
        CacheController controller = new CacheController(service);

        // Assert
        assertNotNull(controller, "Controller should be created successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "All caches cleared successfully.",
            "Failed to clear caches.",
            "Unsupported CacheManager implementation."
    })
    void clearCache_shouldReturnOkResponse_withVariousServiceMessages(String expectedMessage) {
        // Arrange
        when(cacheService.clearCache()).thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = cacheController.clearCache();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be OK");
        assertEquals(expectedMessage, response.getBody(), "Response body should match service message");
        verify(cacheService, times(1)).clearCache();
    }


    @Test
    void clearCache_whenServiceThrowsException_shouldPropagateException() {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Cache service error");
        when(cacheService.clearCache()).thenThrow(expectedException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cacheController.clearCache();
        });

        assertEquals("Cache service error", exception.getMessage());
        verify(cacheService, times(1)).clearCache();
    }

    @Test
    void clearCache_shouldCallServiceOnlyOnce() {
        // Arrange
        when(cacheService.clearCache()).thenReturn("Test message");

        // Act
        cacheController.clearCache();

        // Assert
        verify(cacheService, times(1)).clearCache();
        verifyNoMoreInteractions(cacheService);
    }

    @Test
    void clearCache_multipleCallsShould_callServiceMultipleTimes() {
        // Arrange
        when(cacheService.clearCache()).thenReturn("Cache cleared");

        // Act
        cacheController.clearCache();
        cacheController.clearCache();
        cacheController.clearCache();

        // Assert
        verify(cacheService, times(3)).clearCache();
    }

    @Test
    void clearCache_responseEntity_shouldHaveCorrectStructure() {
        // Arrange
        String message = "Test response message";
        when(cacheService.clearCache()).thenReturn(message);

        // Act
        ResponseEntity<String> response = cacheController.clearCache();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getStatusCode(), "Status code should not be null");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    void clearCache_withNullServiceResponse_shouldHandleGracefully() {
        // Arrange
        when(cacheService.clearCache()).thenReturn(null);

        // Act
        ResponseEntity<String> response = cacheController.clearCache();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should still be OK");
        assertNull(response.getBody(), "Body should be null as returned by service");
        verify(cacheService, times(1)).clearCache();
    }

    @Test
    void clearCache_withEmptyServiceResponse_shouldReturnEmptyString() {
        // Arrange
        when(cacheService.clearCache()).thenReturn("");

        // Act
        ResponseEntity<String> response = cacheController.clearCache();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        assertEquals("", response.getBody(), "Body should be empty string");
        verify(cacheService, times(1)).clearCache();
    }
}
