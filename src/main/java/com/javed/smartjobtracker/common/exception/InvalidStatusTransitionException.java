package com.javed.smartjobtracker.common.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
