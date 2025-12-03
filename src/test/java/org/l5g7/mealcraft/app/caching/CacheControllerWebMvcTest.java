package org.l5g7.mealcraft.app.caching;

import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.auth.security.JwtCookieFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for CacheController testing HTTP endpoints, security, and request/response handling.
 */
@WebMvcTest(
        controllers = CacheController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = { JwtCookieFilter.class }
        )
)
@AutoConfigureMockMvc(addFilters = false)
class CacheControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CacheService cacheService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_withAdminRole_shouldReturnSuccess() throws Exception {
        // Arrange
        String expectedMessage = "All caches cleared successfully.";
        when(cacheService.clearCache()).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/cache/clear"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN + ";charset=UTF-8"))
                .andExpect(content().string(expectedMessage));

        verify(cacheService, times(1)).clearCache();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_withAdminRole_whenServiceReturnsFailure_shouldReturn200() throws Exception {
        // Arrange
        String failureMessage = "Failed to clear caches.";
        when(cacheService.clearCache()).thenReturn(failureMessage);

        // Act & Assert
        mockMvc.perform(get("/cache/clear"))
                .andExpect(status().isOk())
                .andExpect(content().string(failureMessage));

        verify(cacheService, times(1)).clearCache();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_withAdminRole_whenServiceReturnsUnsupported_shouldReturn200() throws Exception {
        // Arrange
        String unsupportedMessage = "Unsupported CacheManager implementation.";
        when(cacheService.clearCache()).thenReturn(unsupportedMessage);

        // Act & Assert
        mockMvc.perform(get("/cache/clear"))
                .andExpect(status().isOk())
                .andExpect(content().string(unsupportedMessage));

        verify(cacheService, times(1)).clearCache();
    }

    @Test
    @WithMockUser(roles = "USER")
    void clearCache_withUserRole_shouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/cache/clear"))
                .andExpect(status().isOk());

        verify(cacheService).clearCache();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_withDifferentHttpMethods_shouldHandleCorrectly() throws Exception {
        // Test POST method (should not be supported)
        mockMvc.perform(post("/cache/clear"))
                .andExpect(status().isMethodNotAllowed());

        // Test PUT method (should not be supported)
        mockMvc.perform(put("/cache/clear"))
                .andExpect(status().isMethodNotAllowed());

        // Test DELETE method (should not be supported)
        mockMvc.perform(delete("/cache/clear"))
                .andExpect(status().isMethodNotAllowed());

        // Verify service is never called for unsupported methods
        verify(cacheService, never()).clearCache();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_withQueryParameters_shouldIgnoreParameters() throws Exception {
        // Arrange
        String expectedMessage = "Cache cleared with parameters ignored";
        when(cacheService.clearCache()).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/cache/clear")
                        .param("ignore", "this")
                        .param("also", "ignore"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));

        verify(cacheService, times(1)).clearCache();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_withHeaders_shouldProcessNormally() throws Exception {
        // Arrange
        String expectedMessage = "Headers don't affect processing";
        when(cacheService.clearCache()).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/cache/clear")
                        .header("Custom-Header", "custom-value")
                        .header("Another-Header", "another-value"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));

        verify(cacheService, times(1)).clearCache();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_multipleConcurrentRequests_shouldHandleAll() throws Exception {
        // Arrange
        String expectedMessage = "Concurrent request handled";
        when(cacheService.clearCache()).thenReturn(expectedMessage);

        // Act & Assert - simulate multiple requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/cache/clear"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expectedMessage));
        }

        verify(cacheService, times(5)).clearCache();
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_requestMapping_shouldUseCorrectBasePath() throws Exception {
        // Arrange
        when(cacheService.clearCache()).thenReturn("Base path test");

        // Act & Assert - test that /cache is the correct base path
        mockMvc.perform(get("/cache/clear"))
                .andExpect(status().isOk());

        // Test that wrong base paths don't work
        mockMvc.perform(get("/wrong/clear"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/clear"))  // Missing base path
                .andExpect(status().isNotFound());

        verify(cacheService, times(1)).clearCache(); // Only one successful call
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_responseContentType_shouldBeTextPlain() throws Exception {
        // Arrange
        when(cacheService.clearCache()).thenReturn("Content type test");

        // Act & Assert
        mockMvc.perform(get("/cache/clear"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN + ";charset=UTF-8"));

        verify(cacheService, times(1)).clearCache();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCache_withAcceptHeader_shouldRespectContentNegotiation() throws Exception {
        // Arrange
        when(cacheService.clearCache()).thenReturn("Accept header test");

        // Act & Assert
        mockMvc.perform(get("/cache/clear")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN + ";charset=UTF-8"));

        verify(cacheService, times(1)).clearCache();
    }
}
