package com.javed.smartjobtracker.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error(ex.getMessage(), 404));
    }


    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<?> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(error(ex.getMessage(), 422));
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(error(ex.getMessage(), 422));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error(ex.getMessage(), 400));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error(message, 400));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("Something went wrong", 500));
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(error(
                        "HTTP method not allowed",
                        405
                ));
    }


    private Map<String, Object> error(String message, int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status);
        map.put("timestamp", Instant.now());
        return map;
    }
}