package org.l5g7.mealcraft.handler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GlobalExceptionHandler focusing on exception handling logic,
 * logging behavior, and response generation.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        // Setup logging capture for testing log statements
        logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.WARN);
    }

    @AfterEach
    void tearDown() {
        // Clean up logging setup
        logger.detachAppender(listAppender);
    }

    @Test
    void handleEntityAlreadyExists_withFullParameters_shouldReturnConflictResponse() {
        // Arrange
        String entityType = "User";
        String fieldName = "email";
        String fieldValue = "test@example.com";
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldName, fieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
        assertNotNull(response.getHeaders());
    }

    @Test
    void handleEntityAlreadyExists_withDefaultFieldName_shouldReturnConflictResponse() {
        // Arrange
        String entityType = "Product";
        String fieldValue = "12345";
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Product already exists", response.getBody());
        assertEquals("id", exception.getFieldName()); // Default field name
        assertEquals(fieldValue, exception.getFieldValue());
    }

    @Test
    void handleEntityAlreadyExists_shouldLogWarningWithCorrectParameters() {
        // Arrange
        String entityType = "Recipe";
        String fieldName = "name";
        String fieldValue = "Chocolate Cake";
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldName, fieldValue);

        // Act
        globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());

        ILoggingEvent logEvent = logsList.get(0);
        assertEquals(Level.WARN, logEvent.getLevel());
        assertEquals("Entity already exists: type={}, field={}, value={}", logEvent.getMessage());

        Object[] arguments = logEvent.getArgumentArray();
        assertEquals(3, arguments.length);
        assertEquals(entityType, arguments[0]);
        assertEquals(fieldName, arguments[1]);
        assertEquals(fieldValue, arguments[2]);
    }

    @Test
    void handleEntityAlreadyExists_withNullFieldValue_shouldHandleGracefully() {
        // Arrange
        String entityType = "User";
        String fieldName = "username";
        String fieldValue = null;
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldName, fieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());

        // Verify logging still works with null value
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        Object[] arguments = logsList.get(0).getArgumentArray();
        assertNull(arguments[2]); // fieldValue should be null
    }

    @Test
    void handleEntityAlreadyExists_withEmptyStrings_shouldHandleCorrectly() {
        // Arrange
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("", "", "");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(" already exists", response.getBody()); // Empty entityType + " already exists"

        // Verify logging
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        Object[] arguments = logsList.get(0).getArgumentArray();
        assertEquals("", arguments[0]); // entityType
        assertEquals("", arguments[1]); // fieldName
        assertEquals("", arguments[2]); // fieldValue
    }

    @Test
    void handleEntityNotFound_shouldReturnNotFoundResponse() {
        // Arrange
        String entityType = "User";
        String fieldName = "id";
        String fieldValue = "999";
        EntityDoesNotExistException exception = new EntityDoesNotExistException(entityType, fieldName, fieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityNotFound(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void handleEntityNotFound_shouldLogWarningWithCorrectParameters() {
        // Arrange
        String entityType = "Product";
        String fieldName = "sku";
        String fieldValue = "ABC123";
        EntityDoesNotExistException exception = new EntityDoesNotExistException(entityType, fieldName, fieldValue);

        // Act
        globalExceptionHandler.handleEntityNotFound(exception, webRequest);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());

        ILoggingEvent logEvent = logsList.get(0);
        assertEquals(Level.WARN, logEvent.getLevel());
        assertEquals("Entity not found: type={}, field={}, value={}", logEvent.getMessage());

        Object[] arguments = logEvent.getArgumentArray();
        assertEquals(entityType, arguments[0]);
        assertEquals(fieldName, arguments[1]);
        assertEquals(fieldValue, arguments[2]);
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequestResponse() {
        // Arrange
        String errorMessage = "Invalid input parameter";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleIllegalArgument(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleIllegalArgument_shouldLogWarningWithException() {
        // Arrange
        String errorMessage = "Parameter cannot be negative";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        globalExceptionHandler.handleIllegalArgument(exception, webRequest);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());

        ILoggingEvent logEvent = logsList.get(0);
        assertEquals(Level.WARN, logEvent.getLevel());
        assertEquals("Bad request: {}", logEvent.getMessage());
        assertEquals(errorMessage, logEvent.getArgumentArray()[0]);
        assertNotNull(logEvent.getThrowableProxy()); // Exception should be logged
    }

    @Test
    void handleIllegalState_shouldReturnBadRequestResponse() {
        // Arrange
        String errorMessage = "Application is in invalid state";
        IllegalStateException exception = new IllegalStateException(errorMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleIllegalState(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleIllegalState_shouldLogWarningWithoutStackTrace() {
        // Arrange
        String errorMessage = "Invalid state transition";
        IllegalStateException exception = new IllegalStateException(errorMessage);

        // Act
        globalExceptionHandler.handleIllegalState(exception, webRequest);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());

        ILoggingEvent logEvent = logsList.get(0);
        assertEquals(Level.WARN, logEvent.getLevel());
        assertEquals("Bad request: {}", logEvent.getMessage());
        assertEquals(errorMessage, logEvent.getArgumentArray()[0]);
        assertNull(logEvent.getThrowableProxy()); // No exception stack trace logged for IllegalStateException
    }

    @Test
    void allExceptionHandlers_shouldReturnResponseEntityWithHeaders() {
        // Test that all handlers return responses with headers

        // EntityAlreadyExistsException
        EntityAlreadyExistsException existsException = new EntityAlreadyExistsException("User", "email", "test@test.com");
        ResponseEntity<Object> existsResponse = globalExceptionHandler.handleEntityAlreadyExists(existsException, webRequest);
        assertNotNull(existsResponse.getHeaders());

        // EntityDoesNotExistException
        EntityDoesNotExistException notFoundException = new EntityDoesNotExistException("User", "id", "999");
        ResponseEntity<Object> notFoundResponse = globalExceptionHandler.handleEntityNotFound(notFoundException, webRequest);
        assertNotNull(notFoundResponse.getHeaders());

        // IllegalArgumentException
        IllegalArgumentException illegalArgException = new IllegalArgumentException("Bad argument");
        ResponseEntity<Object> illegalArgResponse = globalExceptionHandler.handleIllegalArgument(illegalArgException, webRequest);
        assertNotNull(illegalArgResponse.getHeaders());

        // IllegalStateException
        IllegalStateException illegalStateException = new IllegalStateException("Bad state");
        ResponseEntity<Object> illegalStateResponse = globalExceptionHandler.handleIllegalState(illegalStateException, webRequest);
        assertNotNull(illegalStateResponse.getHeaders());
    }

    @Test
    void handleEntityAlreadyExists_withSpecialCharacters_shouldHandleCorrectly() {
        // Arrange
        String entityType = "User";
        String fieldName = "email";
        String fieldValue = "test+special@domain-name.co.uk";
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldName, fieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());

        // Verify special characters are preserved in logging
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(fieldValue, logsList.get(0).getArgumentArray()[2]);
    }
}
