package com.eards.emergency_service.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import com.eards_events.emergency.EmergencyPriority;
import com.eards_events.emergency.EmergencyStatus;
import com.eards_events.emergency.EmergencyType;

public record EmergencyResponse(
        UUID id,
        EmergencyType type,
        EmergencyPriority priority,
        String description,
        BigDecimal latitude,
        BigDecimal longitude,
        EmergencyStatus status,
        String accessCode,
        Timestamp createdAt,
        Timestamp updatedAt) {
}
