package com.eards.emergency_service.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "emergencies")
public class Emergency {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;
    @Column(name = "type", nullable = false)
    private EmergencyType type;

    @Column(name = "priority", nullable = false)
    private EmergencyPriority priority;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "latitude", nullable = true)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = true)
    private BigDecimal longitude;

    @Column(name = "status", nullable = false)
    private EmergencyStatus status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

}
