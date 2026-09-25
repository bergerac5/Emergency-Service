package com.eards.emergency_service.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eards.emergency_service.dto.ApiResponse;
import com.eards.emergency_service.dto.CreateEmergencyRequest;
import com.eards.emergency_service.dto.EmergencyResponse;
import com.eards.emergency_service.service.EmergencyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/emergencies")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmergencyResponse>> createEmergency(
            @Valid @RequestBody CreateEmergencyRequest request) {
        EmergencyResponse response = emergencyService.createEmergency(request);
        ApiResponse<EmergencyResponse> apiResponse = ApiResponse.of("Emergency reported successfully", response);
        return ResponseEntity.created(URI.create("/emergencies/" + response.id())).body(apiResponse);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<EmergencyResponse>> getEmergencyById(
            @PathVariable UUID id) {
        EmergencyResponse response = emergencyService.getEmergencyById(id);
        ApiResponse<EmergencyResponse> apiResponse = ApiResponse.of("Emergency found", response);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/access-code/{accessCode}")
    public ResponseEntity<ApiResponse<EmergencyResponse>> getEmergencyByAccessCode(
            @PathVariable String accessCode) {
        EmergencyResponse response = emergencyService.getEmergencyByAccessCode(accessCode);
        ApiResponse<EmergencyResponse> apiResponse = ApiResponse.of("Emergency found", response);
        return ResponseEntity.ok(apiResponse);
    }

}
