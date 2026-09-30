package com.eards.emergency_service.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eards.emergency_service.dto.ApiResponse;
import com.eards.emergency_service.dto.CreateEmergencyRequest;
import com.eards.emergency_service.dto.EmergencyResponse;
import com.eards.emergency_service.dto.PageResponse;
import com.eards.emergency_service.dto.UpdateEmergencyStatusRequest;
import com.eards.emergency_service.models.EmergencyStatus;
import com.eards.emergency_service.service.EmergencyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/emergencies")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;

    // Create a new emergency report
    @PostMapping
    public ResponseEntity<ApiResponse<EmergencyResponse>> createEmergency(
            @Valid @RequestBody CreateEmergencyRequest request) {
        EmergencyResponse response = emergencyService.createEmergency(request);
        ApiResponse<EmergencyResponse> apiResponse = ApiResponse.of("Emergency reported successfully", response);
        return ResponseEntity.created(URI.create("/emergencies/" + response.id())).body(apiResponse);
    }

    // Get emergency by ID
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<EmergencyResponse>> getEmergencyById(
            @PathVariable UUID id) {
        EmergencyResponse response = emergencyService.getEmergencyById(id);
        ApiResponse<EmergencyResponse> apiResponse = ApiResponse.of("Emergency found", response);
        return ResponseEntity.ok(apiResponse);
    }

    // Track emergency by access code
    @PostMapping("/track/{accessCode}")
    public ResponseEntity<ApiResponse<EmergencyResponse>> getEmergencyByAccessCode(
            @PathVariable String accessCode) {
        EmergencyResponse response = emergencyService.getEmergencyByAccessCode(accessCode);
        ApiResponse<EmergencyResponse> apiResponse = ApiResponse.of("Emergency found", response);
        return ResponseEntity.ok(apiResponse);
    }

    // Get all emergencies with optional filtering and pagination
    @PostMapping("/retiveAll")
    public ResponseEntity<ApiResponse<PageResponse<EmergencyResponse>>> list(
            @RequestParam(required = false) EmergencyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        int safeSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        ApiResponse<PageResponse<EmergencyResponse>> body = ApiResponse.of("Emergencies retrieved successfully",
                emergencyService.listEmergencies(status, pageable));
        return ResponseEntity.ok(body);
    }

    // Update emergency status
    @PatchMapping("/update/status")
    public ResponseEntity<ApiResponse<EmergencyResponse>> updateStatus(
            @Valid @RequestBody UpdateEmergencyStatusRequest request) {
        ApiResponse<EmergencyResponse> body = ApiResponse.of("Emergency status updated",
                emergencyService.updateStatus(request));
        return ResponseEntity.ok(body);
    }

}
