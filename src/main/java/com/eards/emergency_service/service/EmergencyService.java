package com.eards.emergency_service.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eards.emergency_service.dto.CreateEmergencyRequest;
import com.eards.emergency_service.dto.EmergencyResponse;
import com.eards.emergency_service.dto.PageResponse;
import com.eards.emergency_service.exceptions.AccessCodeNotFound;
import com.eards.emergency_service.exceptions.EmergencyNotFoundException;
import com.eards.emergency_service.exceptions.InvalidStatusTransitionException;
import com.eards.emergency_service.mapper.EmergencyMapper;
import com.eards.emergency_service.models.Emergency;
import com.eards.emergency_service.models.EmergencyStatus;
import com.eards.emergency_service.repositories.EmergencyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmergencyService {

    private final EmergencyRepository emergencyRepository;

    private final EmergencyMapper emergencyMapper;

    @Transactional
    public EmergencyResponse createEmergency(CreateEmergencyRequest emergencyRequest) {
        Emergency emergency = emergencyMapper.toEntity(emergencyRequest);
        Emergency savedEmergency = emergencyRepository.save(emergency);
        return emergencyMapper.toResponse(savedEmergency);
    }

    @Transactional(readOnly = true)
    public EmergencyResponse getEmergencyById(UUID id) {
        Emergency emergency = emergencyRepository.findById(id)
                .orElseThrow(() -> new EmergencyNotFoundException(id));
        return emergencyMapper.toResponse(emergency);
    }

    @Transactional(readOnly = true)
    public EmergencyResponse getEmergencyByAccessCode(String accessCode) {
        Emergency emergency = emergencyRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new AccessCodeNotFound(accessCode));
        return emergencyMapper.toResponse(emergency);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmergencyResponse> listEmergencies(EmergencyStatus status, Pageable pageable) {
        Page<Emergency> page = (status == null)
                ? emergencyRepository.findAll(pageable)
                : emergencyRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(emergencyMapper::toResponse));
    }

    @Transactional
    public EmergencyResponse updateStatus(UUID id, EmergencyStatus newStatus) {
        Emergency emergency = findOrThrow(id);

        if (!emergency.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(emergency.getStatus(), newStatus);
        }

        emergency.setStatus(newStatus);
        return emergencyMapper.toResponse(emergencyRepository.save(emergency));
    }

    private Emergency findOrThrow(UUID id) {
        return emergencyRepository.findById(id)
                .orElseThrow(() -> new EmergencyNotFoundException(id));
    }
}
