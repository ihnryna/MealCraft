package org.l5g7.mealcraft.app.caching;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Edge case tests for CacheController to ensure robustness
 * and proper handling of unusual scenarios.
 */
@ExtendWith(MockitoExtension.class)
class CacheControllerEdgeCaseTest {

    @Test
    void constructor_withNullService_shouldNotThrowDuringCreation() {
        // Test that constructor accepts null (though not recommended)
        // This tests defensive programming
        assertDoesNotThrow(() -> {
            new CacheController(null);
        }, "Constructor should not throw exception even with null service");
    }

    @Test
    void clearCache_withNullService_shouldThrowNullPointerException() {
        // Arrange
        CacheController controller = new CacheController(null);

        // Act & Assert
        assertThrows(NullPointerException.class, controller::clearCache, "Should throw NPE when service is null");
    }

    @Test
    void clearCache_serviceMethodCalledTwice_shouldBehavePredictably() {
        // This tests that the controller doesn't cache responses inappropriately
        CacheService mockService = mock(CacheService.class);
        when(mockService.clearCache())
                .thenReturn("First call")
                .thenReturn("Second call");

        CacheController controller = new CacheController(mockService);

        // Act
        ResponseEntity<String> firstResponse = controller.clearCache();
        ResponseEntity<String> secondResponse = controller.clearCache();

        // Assert
        assertEquals("First call", firstResponse.getBody());
        assertEquals("Second call", secondResponse.getBody());
        verify(mockService, times(2)).clearCache();
    }

    @Test
    void clearCache_verifyResponseEntityCreation() {
        // Test that ResponseEntity.ok() is used correctly
        CacheService mockService = mock(CacheService.class);
        String testMessage = "Test message for response entity";
        when(mockService.clearCache()).thenReturn(testMessage);

        CacheController controller = new CacheController(mockService);

        // Act
        ResponseEntity<String> response = controller.clearCache();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(testMessage, response.getBody(), "Body should match service response");
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Status should be successful");
    }

    @Test
    void clearCache_withVeryLongServiceResponse_shouldHandleCorrectly() {
        // Test handling of large responses
        CacheService mockService = mock(CacheService.class);
        String longMessage = "A".repeat(10000); // 10KB string
        when(mockService.clearCache()).thenReturn(longMessage);

        CacheController controller = new CacheController(mockService);

        // Act
        ResponseEntity<String> response = controller.clearCache();

        // Assert
        assertEquals(longMessage, response.getBody());
        assertEquals(10000, response.getBody().length());
    }

    @Test
    void clearCache_withSpecialCharactersInResponse_shouldPreserveCharacters() {
        // Test handling of special characters
        CacheService mockService = mock(CacheService.class);
        String specialMessage = "Cache cleared! 🎉 Special chars: àáâãäåæçèéêë ñ 中文 русский";
        when(mockService.clearCache()).thenReturn(specialMessage);

        CacheController controller = new CacheController(mockService);

        // Act
        ResponseEntity<String> response = controller.clearCache();

        // Assert
        assertEquals(specialMessage, response.getBody());
    }

    @Test
    void clearCache_concurrentAccess_shouldBeSafe() throws InterruptedException {
        // Test thread safety of the controller
        CacheService mockService = mock(CacheService.class);
        when(mockService.clearCache()).thenReturn("Thread safe response");

        CacheController controller = new CacheController(mockService);

        Thread[] threads = new Thread[10];
        ResponseEntity<String>[] responses = new ResponseEntity[10];
        Exception[] exceptions = new Exception[10];

        // Create and start threads
        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    responses[index] = controller.clearCache();
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for completion
        for (Thread thread : threads) {
            thread.join(1000); // 1 second timeout
        }

        // Assert
        for (int i = 0; i < 10; i++) {
            assertNull(exceptions[i], "Thread " + i + " should not throw exception");
            assertNotNull(responses[i], "Response " + i + " should not be null");
        }

        verify(mockService, times(10)).clearCache();
    }

    @Test
    void clearCache_memoryUsage_shouldNotLeakMemory() {
        // Basic test to ensure no obvious memory leaks
        CacheService mockService = mock(CacheService.class);
        when(mockService.clearCache()).thenReturn("Memory test");

        CacheController controller = new CacheController(mockService);

        // Act - call many times
        for (int i = 0; i < 1000; i++) {
            ResponseEntity<String> response = controller.clearCache();
            assertNotNull(response);
            // Allow GC to clean up if needed
            if (i % 100 == 0) {
                System.gc();
            }
        }

        // If we reach here without OutOfMemoryError, the test passes
        verify(mockService, times(1000)).clearCache();
    }
}
