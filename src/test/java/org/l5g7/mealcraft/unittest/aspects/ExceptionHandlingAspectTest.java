package org.l5g7.mealcraft.unittest.aspects;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.aop.ExceptionHandlingAspect;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingAspectTest {

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private ExceptionHandlingAspect exceptionHandlingAspect;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        // Set up logger capturing
        logger = (Logger) LoggerFactory.getLogger("exceptionLogger");
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void logException_WithRuntimeException_LogsErrorMessage() {
        // Arrange
        String methodName = "UserService.createUser(..)";
        Object[] args = new Object[]{"testuser", "test@example.com"};
        RuntimeException exception = new RuntimeException("User already exists");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        exceptionHandlingAspect.logException(joinPoint, exception);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.get(0);

        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getMessage().contains("Service exception in method"));
        assertTrue(logEvent.getMessage().contains("with args"));
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
        assertTrue(logEvent.getFormattedMessage().contains("testuser"));
        assertTrue(logEvent.getFormattedMessage().contains("User already exists"));
    }

    @Test
    void logException_WithNullPointerException_LogsErrorMessage() {
        // Arrange
        String methodName = "ProductService.getProduct(..)";
        Object[] args = new Object[]{123L};
        NullPointerException exception = new NullPointerException("Product not found");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        exceptionHandlingAspect.logException(joinPoint, exception);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.get(0);

        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
        assertTrue(logEvent.getFormattedMessage().contains("123"));
        assertTrue(logEvent.getFormattedMessage().contains("Product not found"));
    }

    @Test
    void logException_WithEmptyArgs_LogsEmptyArray() {
        // Arrange
        String methodName = "AuthService.logout()";
        Object[] args = new Object[]{};
        RuntimeException exception = new RuntimeException("Not authenticated");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        exceptionHandlingAspect.logException(joinPoint, exception);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.get(0);

        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
        assertTrue(logEvent.getFormattedMessage().contains("[]"));
        assertTrue(logEvent.getFormattedMessage().contains("Not authenticated"));
    }

    @Test
    void logException_WithMultipleArgs_LogsAllArgs() {
        // Arrange
        String methodName = "OrderService.createOrder(..)";
        Object[] args = new Object[]{1L, "user123", 99.99, "PENDING"};
        IllegalArgumentException exception = new IllegalArgumentException("Invalid order status");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        exceptionHandlingAspect.logException(joinPoint, exception);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.get(0);

        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains("1"));
        assertTrue(logEvent.getFormattedMessage().contains("user123"));
        assertTrue(logEvent.getFormattedMessage().contains("99.99"));
        assertTrue(logEvent.getFormattedMessage().contains("PENDING"));
        assertTrue(logEvent.getFormattedMessage().contains("Invalid order status"));
    }

    @Test
    void logException_WithNullArgs_HandlesGracefully() {
        // Arrange
        String methodName = "Service.method(..)";
        Object[] args = new Object[]{null, "value"};
        Exception exception = new Exception("Test exception");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        exceptionHandlingAspect.logException(joinPoint, exception);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.get(0);

        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains("null"));
        assertTrue(logEvent.getFormattedMessage().contains("value"));
    }

    @Test
    void logException_WithCheckedException_LogsErrorMessage() {
        // Arrange
        String methodName = "FileService.readFile(..)";
        Object[] args = new Object[]{"test.txt"};
        Exception exception = new Exception("File not found");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        exceptionHandlingAspect.logException(joinPoint, exception);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.get(0);

        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
        assertTrue(logEvent.getFormattedMessage().contains("test.txt"));
        assertTrue(logEvent.getFormattedMessage().contains("File not found"));
    }

    @Test
    void logException_WithNestedCause_LogsFullException() {
        // Arrange
        String methodName = "DatabaseService.query(..)";
        Object[] args = new Object[]{"SELECT * FROM users"};
        RuntimeException cause = new RuntimeException("Connection timeout");
        RuntimeException exception = new RuntimeException("Query failed", cause);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        exceptionHandlingAspect.logException(joinPoint, exception);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.get(0);

        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains("Query failed"));
        assertNotNull(logEvent.getThrowableProxy());
    }

    @Test
    void logException_WithComplexObject_LogsObjectToString() {
        // Arrange
        String methodName = "UserService.updateUser(..)";
        TestUser testUser = new TestUser("john", "john@example.com");
        Object[] args = new Object[]{testUser};
        RuntimeException exception = new RuntimeException("Update failed");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        exceptionHandlingAspect.logException(joinPoint, exception);

        // Assert
        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.get(0);

        assertEquals("ERROR", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
    }

    // Helper class for testing
    private static class TestUser {
        private String name;
        private String email;

        public TestUser(String name, String email) {
            this.name = name;
            this.email = email;
        }

        @Override
        public String toString() {
            return "TestUser{name='" + name + "', email='" + email + "'}";
        }
    }
}