package org.l5g7.mealcraft.handler;

import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple example demonstrating how to test the handleEntityAlreadyExists method.
 * This serves as a reference for the basic testing approach.
 */
class SimpleExceptionHandlerExampleTest {

    @Test
    void testHandleEntityAlreadyExists_basicExample() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Create the exception
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("User", "email", "test@example.com");

        // Create a web request (required by the method)
        MockHttpServletRequest request = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(request);

        // Act
        ResponseEntity<Object> response = handler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
        assertNotNull(response.getHeaders());
    }

    @Test
    void testHandleEntityAlreadyExists_withDefaultFieldName() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Create exception using constructor with default field name
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Product", "12345");

        // Create web request
        MockHttpServletRequest request = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(request);

        // Act
        ResponseEntity<Object> response = handler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Product already exists", response.getBody());

        // Verify exception has correct field name (default is "id")
        assertEquals("id", exception.getFieldName());
        assertEquals("12345", exception.getFieldValue());
    }

    @Test
    void testHandleEntityAlreadyExists_verifyExceptionDetails() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        String entityType = "Recipe";
        String fieldName = "title";
        String fieldValue = "Chocolate Cake";
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException(entityType, fieldName, fieldValue);

        MockHttpServletRequest request = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(request);

        // Act
        ResponseEntity<Object> response = handler.handleEntityAlreadyExists(exception, webRequest);

        // Assert - verify response
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Recipe already exists", response.getBody());

        // Assert - verify exception properties are preserved
        assertEquals(entityType, exception.getEntityType());
        assertEquals(fieldName, exception.getFieldName());
        assertEquals(fieldValue, exception.getFieldValue());
    }

    @Test
    void testHandleEntityAlreadyExists_withNullFieldValue() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("User", "username", null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(request);

        // Act
        ResponseEntity<Object> response = handler.handleEntityAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
        assertNull(exception.getFieldValue());
    }
}
