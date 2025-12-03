package org.l5g7.mealcraft.handler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused tests specifically for the handleEntityAlreadyExists method
 * in GlobalExceptionHandler with comprehensive parameter testing.
 */
class HandleEntityAlreadyExistsTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        webRequest = new ServletWebRequest(request);

        // Setup logging capture
        logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.WARN);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void handleEntityAlreadyExists_basicFunctionality_shouldWork() {
        // Arrange
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("User", "email", "test@example.com");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertAll("Response validation",
            () -> assertNotNull(response, "Response should not be null"),
            () -> assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Status should be CONFLICT"),
            () -> assertEquals("User already exists", response.getBody(), "Body should contain correct message"),
            () -> assertNotNull(response.getHeaders(), "Headers should not be null"),
            () -> assertInstanceOf(HttpHeaders.class, response.getHeaders(), "Headers should be HttpHeaders instance")
        );
    }

    @ParameterizedTest
    @MethodSource("provideEntityTypeTestData")
    void handleEntityAlreadyExists_withDifferentEntityTypes_shouldReturnCorrectMessage(
            String entityType, String fieldName, String fieldValue, String expectedMessage) {
        // Arrange
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldName, fieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  ", "\t", "\n"})
    void handleEntityAlreadyExists_withNullAndEmptyEntityType_shouldHandleGracefully(String entityType) {
        // Arrange
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, "field", "value");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        if (entityType == null) {
            assertEquals("null already exists", response.getBody());
        } else {
            assertEquals(entityType + " already exists", response.getBody());
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "test@example.com", "12345", "special+chars@domain.co.uk"})
    void handleEntityAlreadyExists_withDifferentFieldValues_shouldPreserveValues(String fieldValue) {
        // Arrange
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("User", "email", fieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());

        // Verify logging preserves the field value
        List<ILoggingEvent> logEvents = listAppender.list;
        assertEquals(1, logEvents.size());
        Object[] logArgs = logEvents.get(0).getArgumentArray();
        assertEquals(fieldValue, logArgs[2], "Logged field value should match input");
    }

    @Test
    void handleEntityAlreadyExists_loggingBehavior_shouldLogCorrectInformation() {
        // Arrange
        String entityType = "Product";
        String fieldName = "sku";
        String fieldValue = "PROD-12345";
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldName, fieldValue);

        // Act
        globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        List<ILoggingEvent> logEvents = listAppender.list;
        assertEquals(1, logEvents.size(), "Should have exactly one log event");

        ILoggingEvent logEvent = logEvents.get(0);
        assertAll("Log event validation",
            () -> assertEquals(Level.WARN, logEvent.getLevel(), "Log level should be WARN"),
            () -> assertEquals("Entity already exists: type={}, field={}, value={}", logEvent.getMessage(), "Log message template should be correct"),
            () -> assertEquals(3, logEvent.getArgumentArray().length, "Should have 3 log arguments"),
            () -> assertEquals(entityType, logEvent.getArgumentArray()[0], "First argument should be entityType"),
            () -> assertEquals(fieldName, logEvent.getArgumentArray()[1], "Second argument should be fieldName"),
            () -> assertEquals(fieldValue, logEvent.getArgumentArray()[2], "Third argument should be fieldValue")
        );
    }

    @Test
    void handleEntityAlreadyExists_withUnicodeCharacters_shouldHandleCorrectly() {
        // Arrange
        String entityType = "用户"; // Chinese for "user"
        String fieldName = "名字"; // Chinese for "name"
        String fieldValue = "测试用户123";
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldName, fieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("用户 already exists", response.getBody());

        // Verify Unicode characters are preserved in logging
        List<ILoggingEvent> logEvents = listAppender.list;
        Object[] logArgs = logEvents.get(0).getArgumentArray();
        assertEquals(entityType, logArgs[0]);
        assertEquals(fieldName, logArgs[1]);
        assertEquals(fieldValue, logArgs[2]);
    }

    @Test
    void handleEntityAlreadyExists_withVeryLongValues_shouldHandleCorrectly() {
        // Arrange
        String longEntityType = "A".repeat(1000);
        String longFieldName = "B".repeat(500);
        String longFieldValue = "C".repeat(2000);
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(longEntityType, longFieldName, longFieldValue);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(longEntityType + " already exists", response.getBody());

        // Verify long values are handled in logging
        List<ILoggingEvent> logEvents = listAppender.list;
        Object[] logArgs = logEvents.get(0).getArgumentArray();
        assertEquals(longEntityType, logArgs[0]);
        assertEquals(longFieldName, logArgs[1]);
        assertEquals(longFieldValue, logArgs[2]);
    }

    @Test
    void handleEntityAlreadyExists_multipleCallsSameException_shouldBehaveConsistently() {
        // Arrange
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("User", "email", "test@test.com");

        // Act
        ResponseEntity<Object> response1 = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);
        ResponseEntity<Object> response2 = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);
        ResponseEntity<Object> response3 = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertAll("All responses should be identical",
            () -> assertEquals(response1.getStatusCode(), response2.getStatusCode()),
            () -> assertEquals(response1.getStatusCode(), response3.getStatusCode()),
            () -> assertEquals(response1.getBody(), response2.getBody()),
            () -> assertEquals(response1.getBody(), response3.getBody()),
            () -> assertEquals(HttpStatus.CONFLICT, response1.getStatusCode())
        );

        // Verify multiple log entries
        assertEquals(3, listAppender.list.size(), "Should have 3 log entries for 3 calls");
    }

    @Test
    void handleEntityAlreadyExists_responseHeaders_shouldBeProperlySet() {
        // Arrange
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Test", "field", "value");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        HttpHeaders headers = response.getHeaders();
        assertNotNull(headers, "Headers should not be null");
        assertTrue(headers instanceof HttpHeaders, "Headers should be HttpHeaders instance");
        // The actual headers are set by handleExceptionInternal, which is a Spring framework method
        // We verify that it's called correctly by checking the response structure
    }

    @Test
    void handleEntityAlreadyExists_withDefaultConstructor_shouldUseDefaultFieldName() {
        // Arrange - using constructor with default field name
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Order", "12345");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Order already exists", response.getBody());

        // Verify default field name in logging
        List<ILoggingEvent> logEvents = listAppender.list;
        Object[] logArgs = logEvents.get(0).getArgumentArray();
        assertEquals("Order", logArgs[0]); // entityType
        assertEquals("id", logArgs[1]); // default fieldName
        assertEquals("12345", logArgs[2]); // fieldValue
    }

    // Test data provider for parameterized tests
    static Stream<Arguments> provideEntityTypeTestData() {
        return Stream.of(
            Arguments.of("User", "email", "test@example.com", "User already exists"),
            Arguments.of("Product", "sku", "PROD123", "Product already exists"),
            Arguments.of("Order", "id", "12345", "Order already exists"),
            Arguments.of("Category", "name", "Electronics", "Category already exists"),
            Arguments.of("Recipe", "title", "Chocolate Cake", "Recipe already exists"),
            Arguments.of("UPPERCASE", "field", "value", "UPPERCASE already exists"),
            Arguments.of("lowercase", "field", "value", "lowercase already exists"),
            Arguments.of("MixedCase", "field", "value", "MixedCase already exists"),
            Arguments.of("Entity With Spaces", "field", "value", "Entity With Spaces already exists"),
            Arguments.of("123NumericEntity", "field", "value", "123NumericEntity already exists")
        );
    }
}
