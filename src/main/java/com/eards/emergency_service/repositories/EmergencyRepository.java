package com.eards.emergency_service.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eards.emergency_service.models.Emergency;
import com.eards.emergency_service.models.EmergencyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmergencyRepository extends JpaRepository<Emergency, UUID> {

    Optional<Emergency> findByAccessCode(String accessCode);

    Page<Emergency> findByStatus(EmergencyStatus status, Pageable pageable);

}
