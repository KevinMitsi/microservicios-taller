package com.taller.msvc_orchestrator.repositories;

import com.taller.msvc_orchestrator.entities.NotificationDocument;
import com.taller.msvc_orchestrator.entities.NotificationStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;


public interface NotificationRepository extends MongoRepository<NotificationDocument, String> {
    Page<NotificationDocument> findBy(Pageable pageable, Example<NotificationDocument> example);
    List<NotificationDocument> findByStatusAndScheduledAtBefore(NotificationStatus status, Instant before);
}