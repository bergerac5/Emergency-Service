package com.eards.emergency_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eards.emergency_service.dto.CreateEmergencyRequest;
import com.eards.emergency_service.dto.EmergencyResponse;
import com.eards.emergency_service.dto.TrackEmergencyResponse;
import com.eards.emergency_service.models.Emergency;

@Mapper(componentModel = "spring")
public interface EmergencyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "REPORTED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "accessCode", ignore = true)
    @Mapping(target = "version", ignore = true)
    Emergency toEntity(CreateEmergencyRequest request);

    EmergencyResponse toResponse(Emergency emergency);

    TrackEmergencyResponse toTrackResponse(Emergency emergency);

}
