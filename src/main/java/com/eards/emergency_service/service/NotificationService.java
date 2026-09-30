package com.eards.emergency_service.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eards.emergency_service.models.EmergencyStatus;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void notifyStatusChange(UUID emergencyId, EmergencyStatus status) {
        String threadName = Thread.currentThread().getName();

        logger.info("Sending notification for emergency {} status change to {} on thread {}", emergencyId, status,
                threadName);
        // simulate some work
        try {
            Thread.sleep(2000); // simulating a slow external call (SMS/push provider)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("[{}] Notification sent for emergency {}", threadName, emergencyId);
    }

}
