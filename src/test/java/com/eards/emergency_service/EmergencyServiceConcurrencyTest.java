package com.eards.emergency_service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eards.emergency_service.dto.CreateEmergencyRequest;
import com.eards.emergency_service.dto.UpdateEmergencyStatusRequest;
import com.eards.emergency_service.models.EmergencyPriority;
import com.eards.emergency_service.models.EmergencyStatus;
import com.eards.emergency_service.models.EmergencyType;
import com.eards.emergency_service.service.EmergencyService;

@SpringBootTest
public class EmergencyServiceConcurrencyTest {

    @Autowired
    private EmergencyService emergencyService;

    @Test
    void concurrentStatusUpdatesCanLoseAWrite() throws InterruptedException {
        UUID id = emergencyService.createEmergency(new CreateEmergencyRequest(
                EmergencyType.FIRE,
                EmergencyPriority.CRITICAL,
                "Concurrency test emergency",
                null,
                null)).id();

        // Move it to a state with two valid, DIFFERENT next steps,
        // so we can prove which one "won" without ambiguity.

        emergencyService.updateStatus(new UpdateEmergencyStatusRequest(
                id, EmergencyStatus.WAITING_FOR_DISPATCH));

        int threadCount = 2;
        ExecutorService pools = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch go = new CountDownLatch(1);
        AtomicInteger failure = new AtomicInteger(0);

        List<EmergencyStatus> targets = List.of(
                EmergencyStatus.DISPATCHING, // valid from WAITING_FOR_DISPATCH
                EmergencyStatus.CANCELLED); // also valid from WAITING_FOR_DISPATCH

        for (EmergencyStatus target : targets) {
            pools.submit(() -> {
                ready.countDown();
                try {
                    go.await();
                    emergencyService.updateStatus(new UpdateEmergencyStatusRequest(id, target));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    failure.incrementAndGet();
                    // System.out.println("Update failed: " + e.getClass().getSimpleName());
                }
            });
        }

        ready.await();
        go.countDown();
        pools.shutdown();
        pools.awaitTermination(5, TimeUnit.SECONDS);

        EmergencyStatus finalStatus = emergencyService.getEmergencyById(id).status();
        System.out.println("Final Status: " + finalStatus);
        System.out.println("Failed updates: " + failure.get());

        // Without locking: both writes usually "succeed" (0 failures),
        // and finalStatus is just whichever thread committed last —
        // the other thread's update vanished with no error, no trace.
        assertThat(failure.get()).isEqualTo(1);
        assertThat(finalStatus).isIn(targets);

    }

}
