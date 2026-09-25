package com.eards.emergency_service.exceptions;

public class AccessCodeNotFound extends RuntimeException {

    public AccessCodeNotFound(String accessCode) {
        super("Emergency not found with access code: " + accessCode);
    }

}
