package com.eards.emergency_service.exceptions;

import com.eards.emergency_service.models.EmergencyStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(EmergencyStatus from, EmergencyStatus to) {
        super("Cannot change status from " + from + " to " + to);
    }
}
