package com.eards.emergency_service.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.eards.emergency_service.models.EventStatus;
import com.eards.emergency_service.models.OutboxEvents;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvents, UUID> {
    List<OutboxEvents> findTop50ByStatusOrderByCreatedAtAsc(EventStatus status);

}
