package com.eards.emergency_service.outbox;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import com.eards.emergency_service.models.EventStatus;
import com.eards.emergency_service.models.OutboxEvents;
import com.eards.emergency_service.repositories.OutboxRepository;

import lombok.extern.slf4j.Slf4j;

@Component

@Slf4j
public class OutboxRelay {

    private static final String TOPIC = "emergency.events";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxRelay(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    public void publishPending() {
        List<OutboxEvents> batch = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(EventStatus.PENDING);

        for (OutboxEvents event : batch) {
            try {
                ProducerRecord<String, String> record = new ProducerRecord<>(
                        TOPIC,
                        event.getAggregateId().toString(),
                        event.getPayload());
                record.headers().add("eventId",
                        event.getId().toString().getBytes(StandardCharsets.UTF_8));
                record.headers().add("eventType",
                        event.getEventType().getBytes(StandardCharsets.UTF_8));

                kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);

                event.setStatus(EventStatus.PUBLISHED);
                outboxRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
                break;
            }
        }
    }
}
