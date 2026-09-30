package com.eards.emergency_service.exceptions;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String message) {
        super(message);
    }

}
