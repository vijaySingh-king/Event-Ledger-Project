package com.example.gateway.GlobalExceptionHandler;

import com.example.gateway.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> onValidation(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage(), "code", "VALIDATION_ERROR"));
    }

    @ExceptionHandler(DuplicateEventException.class)
    public ResponseEntity<?> onDuplicate(DuplicateEventException ex) {
        // Return original event with 409 CONFLICT
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getExistingEvent());
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, String>> onNotFound(EventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "code", "NOT_FOUND"));
    }

    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> onServiceUnavailable(AccountServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", ex.getMessage(), "code", "SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> onGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error", "code", "INTERNAL_ERROR"));
    }
}

