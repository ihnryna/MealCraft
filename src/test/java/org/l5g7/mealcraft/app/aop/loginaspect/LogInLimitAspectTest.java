package org.l5g7.mealcraft.app.aop.loginaspect;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogInLimitAspectTest {

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private LogInLimitAspect logInLimitAspect;

    private Method writeLogLimitExceededResponseMethod;

    @BeforeEach
    void setUp() throws Exception {
        // Get the private method using reflection
        writeLogLimitExceededResponseMethod = LogInLimitAspect.class
                .getDeclaredMethod("writeLogLimitExceededResponse", ServletRequestAttributes.class);
        writeLogLimitExceededResponseMethod.setAccessible(true);
    }

    @Test
    void writeLogLimitExceededResponse_setsCorrectStatusAndContentType() throws Exception {
        // Arrange
        when(servletRequestAttributes.getResponse()).thenReturn(httpServletResponse);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        // Act
        writeLogLimitExceededResponseMethod.invoke(logInLimitAspect, servletRequestAttributes);

        // Assert
        verify(httpServletResponse).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(httpServletResponse).setContentType("application/json");
        verify(printWriter).write("{\"error\": \"Too many login attempts from this IP address. Please try again later.\"}");
        verify(printWriter).flush();
    }

    @Test
    void writeLogLimitExceededResponse_writesCorrectErrorMessage() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter actualPrintWriter = new PrintWriter(stringWriter);

        when(servletRequestAttributes.getResponse()).thenReturn(httpServletResponse);
        when(httpServletResponse.getWriter()).thenReturn(actualPrintWriter);

        // Act
        writeLogLimitExceededResponseMethod.invoke(logInLimitAspect, servletRequestAttributes);

        // Assert
        actualPrintWriter.flush();
        String writtenContent = stringWriter.toString();
        assertEquals("{\"error\": \"Too many login attempts from this IP address. Please try again later.\"}", writtenContent);

        verify(httpServletResponse).setStatus(429); // TOO_MANY_REQUESTS status code
        verify(httpServletResponse).setContentType("application/json");
    }

    @Test
    void writeLogLimitExceededResponse_handlesWriterException() throws Exception {
        // Arrange
        when(servletRequestAttributes.getResponse()).thenReturn(httpServletResponse);
        when(httpServletResponse.getWriter()).thenThrow(new IOException("Writer error"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            writeLogLimitExceededResponseMethod.invoke(logInLimitAspect, servletRequestAttributes);
        });

        // Verify that the underlying cause is the IOException we threw
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }

        assertTrue(rootCause instanceof IOException);
        assertEquals("Writer error", rootCause.getMessage());
    }

    @Test
    void writeLogLimitExceededResponse_setsResponsePropertiesInCorrectOrder() throws Exception {
        // Arrange
        when(servletRequestAttributes.getResponse()).thenReturn(httpServletResponse);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        // Act
        writeLogLimitExceededResponseMethod.invoke(logInLimitAspect, servletRequestAttributes);

        // Assert - verify method calls happened in the right order
        var inOrder = inOrder(httpServletResponse, printWriter);
        inOrder.verify(httpServletResponse).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        inOrder.verify(httpServletResponse).setContentType("application/json");
        inOrder.verify(httpServletResponse).getWriter();
        inOrder.verify(printWriter).write("{\"error\": \"Too many login attempts from this IP address. Please try again later.\"}");
        inOrder.verify(printWriter).flush();
    }

    @Test
    void writeLogLimitExceededResponse_verifyHttpStatusCode() throws Exception {
        // Arrange
        when(servletRequestAttributes.getResponse()).thenReturn(httpServletResponse);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        // Act
        writeLogLimitExceededResponseMethod.invoke(logInLimitAspect, servletRequestAttributes);

        // Assert - specifically verify the status code is 429 (TOO_MANY_REQUESTS)
        verify(httpServletResponse).setStatus(429);
        assertEquals(429, HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    void writeLogLimitExceededResponse_verifyJsonStructure() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter actualPrintWriter = new PrintWriter(stringWriter);

        when(servletRequestAttributes.getResponse()).thenReturn(httpServletResponse);
        when(httpServletResponse.getWriter()).thenReturn(actualPrintWriter);

        // Act
        writeLogLimitExceededResponseMethod.invoke(logInLimitAspect, servletRequestAttributes);

        // Assert
        actualPrintWriter.flush();
        String jsonResponse = stringWriter.toString();

        // Verify it's valid JSON structure
        assertTrue(jsonResponse.startsWith("{\"error\":"));
        assertTrue(jsonResponse.endsWith("\"}"));
        assertTrue(jsonResponse.contains("Too many login attempts from this IP address"));
        assertTrue(jsonResponse.contains("Please try again later"));
    }

    @Test
    void writeLogLimitExceededResponse_ensuresWriterFlushIsCalled() throws Exception {
        // Arrange
        when(servletRequestAttributes.getResponse()).thenReturn(httpServletResponse);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        // Act
        writeLogLimitExceededResponseMethod.invoke(logInLimitAspect, servletRequestAttributes);

        // Assert - verify flush is called to ensure response is sent immediately
        verify(printWriter, times(1)).flush();
    }

    @Test
    void writeLogLimitExceededResponse_handlesServletRequestAttributesNull() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            writeLogLimitExceededResponseMethod.invoke(logInLimitAspect, (ServletRequestAttributes) null);
        }, "Should throw exception when ServletRequestAttributes is null");
    }
}
