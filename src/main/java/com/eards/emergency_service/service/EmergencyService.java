package com.eards.emergency_service.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eards.emergency_service.dto.CreateEmergencyRequest;
import com.eards.emergency_service.dto.EmergencyResponse;
import com.eards.emergency_service.dto.PageResponse;
import com.eards.emergency_service.dto.UpdateEmergencyStatusRequest;
import com.eards.emergency_service.exceptions.AccessCodeNotFound;
import com.eards.emergency_service.exceptions.EmergencyNotFoundException;
import com.eards.emergency_service.exceptions.InvalidStatusTransitionException;
import com.eards.emergency_service.mapper.EmergencyMapper;
import com.eards.emergency_service.models.Emergency;

import com.eards.emergency_service.models.OutboxEvents;
import com.eards.emergency_service.repositories.EmergencyRepository;
import com.eards.emergency_service.repositories.OutboxRepository;
import com.eards_events.emergency.EmergencyCreated;
import com.eards_events.emergency.EmergencyEventTypes;
import com.eards_events.emergency.EmergencyStatus;
import com.eards_events.envelope.EventEnvelope;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service

public class EmergencyService {

    private final EmergencyRepository emergencyRepository;

    private final EmergencyMapper emergencyMapper;

    private final NotificationService notificationService;

    private final ObjectMapper objectMapper;

    private final OutboxRepository outboxRepository;

    public EmergencyService(EmergencyRepository emergencyRepository, EmergencyMapper emergencyMapper,
            NotificationService notificationService, ObjectMapper objectMapper, OutboxRepository outboxRepository) {
        this.emergencyRepository = emergencyRepository;
        this.emergencyMapper = emergencyMapper;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
        this.outboxRepository = outboxRepository;
    }

    // create emergency
    @Transactional
    public EmergencyResponse createEmergency(CreateEmergencyRequest emergencyRequest) {
        Emergency emergency = emergencyMapper.toEntity(emergencyRequest);
        Emergency savedEmergency = emergencyRepository.save(emergency);

        UUID eventId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        // Create the EmergencyCreated event
        EmergencyCreated data = new EmergencyCreated(
                savedEmergency.getId(),
                savedEmergency.getType(),
                savedEmergency.getPriority(),
                savedEmergency.getDescription(),
                savedEmergency.getLatitude(),
                savedEmergency.getLongitude(),
                savedEmergency.getStatus(),
                savedEmergency.getVersion());

        // Create the event envelope
        EventEnvelope<EmergencyCreated> envelope = new EventEnvelope<>(
                eventId,
                EmergencyEventTypes.EMERGENCY_CREATED,
                1,
                Instant.now(),
                savedEmergency.getId().toString(),
                correlationId.toString(),
                "emergency-service",
                data);

        OutboxEvents outbox = new OutboxEvents();
        outbox.setId(eventId);
        outbox.setAggregateId(savedEmergency.getId()); // UUID, from the entity
        outbox.setAggregateType("Emergency");
        outbox.setEventType(envelope.eventType());
        outbox.setEventVersion(envelope.eventVersion());
        outbox.setPayload(objectMapper.writeValueAsString(envelope));
        outboxRepository.save(outbox);

        return emergencyMapper.toResponse(savedEmergency);
    }

    // get emergency by id
    @Transactional(readOnly = true)
    public EmergencyResponse getEmergencyById(UUID id) {
        Emergency emergency = emergencyRepository.findById(id)
                .orElseThrow(() -> new EmergencyNotFoundException(id));
        return emergencyMapper.toResponse(emergency);
    }

    // get emergency by access code
    @Transactional(readOnly = true)
    public EmergencyResponse getEmergencyByAccessCode(String accessCode) {
        Emergency emergency = emergencyRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new AccessCodeNotFound(accessCode));
        return emergencyMapper.toResponse(emergency);
    }

    // list emergencies with optional status filter
    @Transactional(readOnly = true)
    public PageResponse<EmergencyResponse> listEmergencies(EmergencyStatus status, Pageable pageable) {
        Page<Emergency> page = (status == null)
                ? emergencyRepository.findAll(pageable)
                : emergencyRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(emergencyMapper::toResponse));
    }

    // update emergency status
    @Transactional
    public EmergencyResponse updateStatus(UpdateEmergencyStatusRequest statusRequest) {
        Emergency emergency = findOrThrow(statusRequest.id());

        if (!emergency.getStatus().canTransitionTo(statusRequest.status())) {
            throw new InvalidStatusTransitionException(
                    emergency.getStatus(), statusRequest.status());
        }

        emergency.setStatus(statusRequest.status());
        Emergency saved = emergencyRepository.save(emergency);

        notificationService.notifyStatusChange(saved.getId(), saved.getStatus());

        return emergencyMapper.toResponse(saved);
    }

    private Emergency findOrThrow(UUID id) {
        return emergencyRepository.findById(id)
                .orElseThrow(() -> new EmergencyNotFoundException(id));
    }
}
