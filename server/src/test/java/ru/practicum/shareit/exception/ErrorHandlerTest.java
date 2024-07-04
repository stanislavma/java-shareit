package ru.practicum.shareit.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleValidationException() {
        ValidationException exception = new ValidationException("Validation error", HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = errorHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error", response.getBody().getError());
    }

    @Test
    void handleEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        ResponseEntity<ErrorResponse> response = errorHandler.handleEntityNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody().getError());
    }

    @Test
    void handleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = errorHandler.handleAccessDeniedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getError());
    }

    @Test
    void handleConstraintViolationException() {
        SQLException sqlException = new SQLException("Constraint violation");
        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", sqlException, "constraintName");

        ResponseEntity<ErrorResponse> response = errorHandler.handleConstraintViolationException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Constraint violation", response.getBody().getError());
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");

        ResponseEntity<ErrorResponse> response = errorHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Illegal argument", response.getBody().getError());
    }

}
