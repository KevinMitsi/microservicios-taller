package com.taller.msvc_orchestrator.controllers;

import com.taller.msvc_orchestrator.DTO.NotificationCreateRequest;
import com.taller.msvc_orchestrator.DTO.NotificationSearchCriteria;
import com.taller.msvc_orchestrator.entities.ChannelEntity;
import com.taller.msvc_orchestrator.entities.NotificationDocument;
import com.taller.msvc_orchestrator.services.ChannelService;
import com.taller.msvc_orchestrator.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ChannelService channelService;

    @GetMapping("/channels")
    public List<ChannelEntity> getChannels() { return channelService.getAvailableChannels(); }

    @PostMapping
    public ResponseEntity<NotificationDocument> createAndSend(@RequestBody NotificationCreateRequest req) {
        NotificationDocument saved = notificationService.createAndSend(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/schedule")
    public ResponseEntity<NotificationDocument> schedule(@RequestBody NotificationCreateRequest req,
                                                         @RequestParam("sendAt") Instant sendAt) {
        NotificationDocument doc = notificationService.schedule(req, sendAt);
        return new ResponseEntity<>(doc, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<NotificationDocument> search(NotificationSearchCriteria criteria, Pageable p) {
        return notificationService.search(criteria, p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDocument> getById(@PathVariable String id) {
        return notificationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable String id) {
        notificationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
