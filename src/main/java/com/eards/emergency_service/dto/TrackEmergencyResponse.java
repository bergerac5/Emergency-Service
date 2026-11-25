package com.eards.emergency_service.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.eards_events.emergency.EmergencyPriority;
import com.eards_events.emergency.EmergencyStatus;
import com.eards_events.emergency.EmergencyType;

public record TrackEmergencyResponse(
        EmergencyType type,
        EmergencyPriority priority,
        String description,
        BigDecimal latitude,
        BigDecimal longitude,
        EmergencyStatus status,
        Timestamp createdAt,
        Timestamp updatedAt) {

}
