package com.eards.emergency_service.models;

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
@Table(name = "processed_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessedEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;
    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(name = "processed_at", nullable = false)
    private Timestamp processedAt;
}
