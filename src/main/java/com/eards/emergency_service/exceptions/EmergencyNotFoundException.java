package com.eards.emergency_service.exceptions;

import java.util.UUID;

public class EmergencyNotFoundException extends RuntimeException {

    public EmergencyNotFoundException(UUID id) {
        super("Emergency not found with id: " + id);
    }

}
