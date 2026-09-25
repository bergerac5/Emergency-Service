package com.eards.emergency_service.dto;

import com.eards.emergency_service.models.EmergencyStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateEmergencyStatusRequest(
        @NotNull(message = "Status is required") EmergencyStatus status) {
}
