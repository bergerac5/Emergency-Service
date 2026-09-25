package com.eards.emergency_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eards.emergency_service.dto.CreateEmergencyRequest;
import com.eards.emergency_service.dto.EmergencyResponse;
import com.eards.emergency_service.exceptions.AccessCodeNotFound;
import com.eards.emergency_service.exceptions.EmergencyNotFoundException;
import com.eards.emergency_service.mapper.EmergencyMapper;
import com.eards.emergency_service.models.Emergency;
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

    public EmergencyResponse getEmergencyById(UUID id) {
        Emergency emergency = emergencyRepository.findById(id)
                .orElseThrow(() -> new EmergencyNotFoundException(id));
        return emergencyMapper.toResponse(emergency);
    }

    public EmergencyResponse getEmergencyByAccessCode(String accessCode) {
        Emergency emergency = emergencyRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new AccessCodeNotFound(accessCode));
        return emergencyMapper.toResponse(emergency);
    }
}
