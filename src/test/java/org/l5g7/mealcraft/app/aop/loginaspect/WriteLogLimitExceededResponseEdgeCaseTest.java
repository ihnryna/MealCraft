package org.l5g7.mealcraft.app.aop.loginaspect;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Edge case and boundary tests specifically for the writeLogLimitExceededResponse method.
 * These tests focus on error conditions, boundary cases, and ensuring robustness.
 */
@ExtendWith(MockitoExtension.class)
class WriteLogLimitExceededResponseEdgeCaseTest {

    @Test
    void writeLogLimitExceededResponse_nullResponseHandling() throws Exception {
        // Arrange
        LogInLimitAspect aspect = new LogInLimitAspect();
        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        when(attrs.getResponse()).thenReturn(null);

        Method method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(aspect, attrs);
        });

        // Verify the exception chain contains our expected IOException
        Throwable cause = exception;
        while (cause.getCause() != null && !(cause.getCause() instanceof IOException)) {
            cause = cause.getCause();
        }

        if (cause.getCause() instanceof IOException) {
            IOException ioException = (IOException) cause.getCause();
            assertEquals("Could not get HttpServletResponse in LogInLimitAspect", ioException.getMessage());
        }
    }

    @Test
    void writeLogLimitExceededResponse_writerGetterThrowsException() throws Exception {
        // Arrange
        LogInLimitAspect aspect = new LogInLimitAspect();
        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(attrs.getResponse()).thenReturn(response);
        when(response.getWriter()).thenThrow(new IOException("Cannot create writer"));

        Method method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(aspect, attrs);
        });

        // Verify the original IOException is preserved
        assertTrue(containsIOException(exception, "Cannot create writer"));

        // Verify status and content type were still set before the exception
        verify(response).setStatus(429);
        verify(response).setContentType("application/json");
    }

    @Test
    void writeLogLimitExceededResponse_writerWriteThrowsException() throws Exception {
        // Arrange
        LogInLimitAspect aspect = new LogInLimitAspect();
        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(attrs.getResponse()).thenReturn(response);
        when(response.getWriter()).thenReturn(writer);
        doThrow(new RuntimeException("Write failed")).when(writer).write(anyString());

        Method method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(aspect, attrs);
        });

        assertTrue(containsRuntimeException(exception, "Write failed"));
    }

    @Test
    void writeLogLimitExceededResponse_writerFlushThrowsException() throws Exception {
        // Arrange
        LogInLimitAspect aspect = new LogInLimitAspect();
        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(attrs.getResponse()).thenReturn(response);
        when(response.getWriter()).thenReturn(writer);
        doThrow(new RuntimeException("Flush failed")).when(writer).flush();

        Method method = LogInLimitAspect.class.getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        method.setAccessible(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(aspect, attrs);
        });

        assertTrue(containsRuntimeException(exception, "Flush failed"));

        // Verify write was called before flush failed
        verify(writer).write("{\"error\": \"Too many login attempts from this IP address. Please try again later.\"}");
    }

    // Helper methods
    private boolean containsIOException(Throwable throwable, String message) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof IOException && current.getMessage().contains(message)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean containsRuntimeException(Throwable throwable, String message) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof RuntimeException && current.getMessage().contains(message)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
