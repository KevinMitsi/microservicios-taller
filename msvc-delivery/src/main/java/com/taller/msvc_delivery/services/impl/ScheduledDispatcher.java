package com.taller.msvc_delivery.services.impl;

import com.taller.msvc_delivery.DTO.NotificationDTO;
import com.taller.msvc_delivery.entities.NotificationDocument;
import com.taller.msvc_delivery.entities.NotificationStatus;
import com.taller.msvc_delivery.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledDispatcher {

    private final NotificationRepository repo;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelayString = "${notifications.scheduler.poll-ms:60000}")
    public void publishDue() {
        Instant now = Instant.now();
        List<NotificationDocument> due = repo.findByStatusAndScheduledAtBefore(NotificationStatus.SCHEDULED, now);
        for (NotificationDocument n : due) {
            try {
                n.setStatus(NotificationStatus.PENDING);
                repo.save(n);
                NotificationDTO dto = n.toDto();
                rabbitTemplate.convertAndSend("exchange.notifications", n.getChannel(), dto);
                log.info("Queued scheduled notification {}", n.getId());
            } catch (Exception e) {
                log.error("Error publishing scheduled notification {}: {}", n.getId(), e.getMessage());
            }
        }
    }
}
