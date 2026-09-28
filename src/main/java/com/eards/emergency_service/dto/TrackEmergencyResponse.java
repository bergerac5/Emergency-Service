package com.eards.emergency_service.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.eards.emergency_service.models.EmergencyPriority;
import com.eards.emergency_service.models.EmergencyStatus;
import com.eards.emergency_service.models.EmergencyType;

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
