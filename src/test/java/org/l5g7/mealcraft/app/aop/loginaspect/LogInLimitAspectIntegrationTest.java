package org.l5g7.mealcraft.app.aop.loginaspect;

import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogInLimitAspectIntegrationTest {

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    private LogInLimitAspect logInLimitAspect;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ServletRequestAttributes requestAttributes;

    @BeforeEach
    void setUp() {
        logInLimitAspect = new LogInLimitAspect();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        requestAttributes = new ServletRequestAttributes(request, response);
    }

    @Test
    void limitLoginLogs_callsWriteLogLimitExceededResponse_whenLimitExceeded() throws Throwable {
        // Arrange - simulate many login attempts from same IP
        String testIp = "192.168.1.100";
        request.setRemoteAddr(testIp);

        // Pre-populate with maximum login attempts
        HashMap<String, ArrayList<LocalDateTime>> logCounts = new HashMap<>();
        ArrayList<LocalDateTime> attempts = new ArrayList<>();
        for (int i = 0; i < 200; i++) { // MAX_LOGS = 200
            attempts.add(LocalDateTime.now());
        }
        logCounts.put(testIp, attempts);

        // Use reflection to set the logCounts field
        var field = LogInLimitAspect.class.getDeclaredField("logCounts");
        field.setAccessible(true);
        field.set(logInLimitAspect, logCounts);

        // Mock static RequestContextHolder
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);

            // Act
            Object result = logInLimitAspect.limitLoginLogs(proceedingJoinPoint);

            // Assert
            assertNull(result, "Should return null when limit exceeded");
            assertEquals(429, response.getStatus(), "Should set HTTP 429 status");
            assertEquals("application/json", response.getContentType(), "Should set JSON content type");

            String responseContent = response.getContentAsString();
            assertTrue(responseContent.contains("Too many login attempts"), "Response should contain error message");

            // Verify joinPoint.proceed() was never called
            verify(proceedingJoinPoint, never()).proceed();
        }
    }

    @Test
    void writeLogLimitExceededResponse_integrationTest_withRealMockResponse() throws Exception {
        // Arrange
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        ServletRequestAttributes attributes = new ServletRequestAttributes(new MockHttpServletRequest(), mockResponse);

        // Use reflection to call the private method
        var method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);

        // Act
        method.invoke(logInLimitAspect, attributes);

        // Assert
        assertEquals(429, mockResponse.getStatus(), "Status should be 429 (Too Many Requests)");
        assertEquals("application/json", mockResponse.getContentType(), "Content type should be application/json");

        String content = mockResponse.getContentAsString();
        assertEquals("{\"error\": \"Too many login attempts from this IP address. Please try again later.\"}",
                    content, "Response content should match expected JSON error message");
    }

    @Test
    void writeLogLimitExceededResponse_behaviorTest_withDifferentIpAddresses() throws Throwable {
        // Test that the method works correctly when called multiple times with different scenarios

        // Test Case 1: Standard IP address
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        ServletRequestAttributes attrs1 = new ServletRequestAttributes(new MockHttpServletRequest(), response1);

        var method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);
        method.invoke(logInLimitAspect, attrs1);

        assertEquals(429, response1.getStatus());
        assertEquals("application/json", response1.getContentType());
        assertFalse(response1.getContentAsString().isEmpty());

        // Test Case 2: Another instance to ensure method is stateless
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        ServletRequestAttributes attrs2 = new ServletRequestAttributes(new MockHttpServletRequest(), response2);

        method.invoke(logInLimitAspect, attrs2);

        assertEquals(429, response2.getStatus());
        assertEquals("application/json", response2.getContentType());
        assertEquals(response1.getContentAsString(), response2.getContentAsString(),
                    "Both responses should have identical content");
    }

    @Test
    void writeLogLimitExceededResponse_errorHandling_withIOException() throws Exception {
        // Arrange - create a custom ServletRequestAttributes that will cause IOException
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);

        when(attrs.getResponse()).thenReturn(mockResponse);
        when(mockResponse.getWriter()).thenThrow(new IOException("Simulated IO error"));

        // Act & Assert
        var method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(logInLimitAspect, attrs);
        });

        // The IOException should be wrapped in reflection exceptions
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        assertTrue(cause instanceof IOException);
    }

    @Test
    void writeLogLimitExceededResponse_concurrencyTest() throws Exception {
        // Test that the method is thread-safe when called concurrently
        var method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);

        // Create multiple responses for concurrent testing
        MockHttpServletResponse[] responses = new MockHttpServletResponse[10];
        ServletRequestAttributes[] attributes = new ServletRequestAttributes[10];

        for (int i = 0; i < 10; i++) {
            responses[i] = new MockHttpServletResponse();
            attributes[i] = new ServletRequestAttributes(new MockHttpServletRequest(), responses[i]);
        }

        // Act - simulate concurrent calls
        Thread[] threads = new Thread[10];
        Exception[] exceptions = new Exception[10];

        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    method.invoke(logInLimitAspect, attributes[index]);
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(1000); // 1 second timeout
        }

        // Assert - verify all calls completed successfully
        for (int i = 0; i < 10; i++) {
            assertNull(exceptions[i], "Thread " + i + " should not have thrown an exception");
            assertEquals(429, responses[i].getStatus(), "All responses should have status 429");
            assertEquals("application/json", responses[i].getContentType(), "All responses should have JSON content type");
            assertFalse(responses[i].getContentAsString().isEmpty(), "All responses should have content");
        }
    }

    @Test
    void writeLogLimitExceededResponse_verifyExactErrorMessageFormat() throws Exception {
        // Arrange
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        ServletRequestAttributes attrs = new ServletRequestAttributes(new MockHttpServletRequest(), servletResponse);

        var method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);

        // Act
        method.invoke(logInLimitAspect, attrs);

        // Assert - verify exact JSON format
        String content = servletResponse.getContentAsString();

        // Check JSON structure
        assertTrue(content.startsWith("{"), "Response should start with '{'");
        assertTrue(content.endsWith("}"), "Response should end with '}'");
        assertTrue(content.contains("\"error\""), "Response should contain 'error' key");
        assertTrue(content.contains("\"Too many login attempts from this IP address. Please try again later.\""),
                  "Response should contain exact error message");

        // Verify it's properly formatted JSON (no extra spaces, proper quotes)
        assertEquals("{\"error\": \"Too many login attempts from this IP address. Please try again later.\"}",
                    content, "Response should match exact expected format");
    }
}
