package com.eards.emergency_service.models;

import java.sql.Timestamp;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "outbox_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OutboxEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(name = "event_version", nullable = false)
    private Integer eventVersion;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private JsonNode payload;
    @Column(name = "status", nullable = false)
    private EventStatus status;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

}
