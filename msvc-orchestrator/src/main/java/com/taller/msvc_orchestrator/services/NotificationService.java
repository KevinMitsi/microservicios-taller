package com.taller.msvc_orchestrator.services;

import com.taller.msvc_orchestrator.DTO.NotificationCreateRequest;
import com.taller.msvc_orchestrator.DTO.NotificationSearchCriteria;
import com.taller.msvc_orchestrator.entities.NotificationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface NotificationService {
    NotificationDocument createAndSend(NotificationCreateRequest req);
    NotificationDocument schedule(NotificationCreateRequest req, Instant sendAt);
    Page<NotificationDocument> search(NotificationSearchCriteria criteria, Pageable pageable);
    Optional<NotificationDocument> findById(String id);
    void cancel(String id);
}
