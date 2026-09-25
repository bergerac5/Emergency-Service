package com.eards.emergency_service.dto;

import java.math.BigDecimal;

import com.eards.emergency_service.models.EmergencyPriority;
import com.eards.emergency_service.models.EmergencyType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEmergencyRequest(
                @NotNull(message = "Emergency type is required") EmergencyType type,
                @NotNull(message = "Priority is required") EmergencyPriority priority,
                @NotBlank(message = "Description is required") @Size(max = 255, message = "Description must not exceed 255 characters") String description,
                @DecimalMin(value = "-90.0", message = "Latitude must be >= -90") @DecimalMax(value = "90.0", message = "Latitude must be <= 90") BigDecimal latitude,
                @DecimalMin(value = "-180.0", message = "Longitude must be >= -180") @DecimalMax(value = "180.0", message = "Longitude must be <= 180") BigDecimal longitude) {

}
