package com.eards.emergency_service.dto;

import java.util.UUID;

import com.eards_events.emergency.EmergencyStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateEmergencyStatusRequest(
        @NotNull(message = "Emergency ID is required") UUID id,
        @NotNull(message = "Status is required") EmergencyStatus status) {
}
