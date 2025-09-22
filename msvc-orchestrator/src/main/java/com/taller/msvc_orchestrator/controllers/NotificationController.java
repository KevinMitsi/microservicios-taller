package com.taller.msvc_orchestrator.controllers;

import com.taller.msvc_orchestrator.DTO.NotificationCreateRequest;
import com.taller.msvc_orchestrator.DTO.NotificationSearchCriteria;
import com.taller.msvc_orchestrator.entities.ChannelEntity;
import com.taller.msvc_orchestrator.entities.NotificationDocument;
import com.taller.msvc_orchestrator.entities.TemplateEntity;
import com.taller.msvc_orchestrator.entities.NotificationStatus;
import com.taller.msvc_orchestrator.services.ChannelService;
import com.taller.msvc_orchestrator.services.NotificationService;
import com.taller.msvc_orchestrator.services.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ChannelService channelService;
    private final TemplateService templateService;

    @GetMapping("/channels")
    public List<ChannelEntity> getChannels() { return channelService.getAvailableChannels(); }

    @PostMapping
    public ResponseEntity<NotificationDocument> createAndSend(@Valid @RequestBody NotificationCreateRequest req) {
        NotificationDocument saved;
        if (req.getSendAt() != null) {
            saved = notificationService.schedule(req, req.getSendAt());
        } else {
            saved = notificationService.createAndSend(req);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @GetMapping("/templates")
    public List<TemplateEntity> getTemplates() {
        return templateService.getAvailableTemplates();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) String message) {

        Pageable paging = PageRequest.of(page, size);

        // Crear el objeto criteria con los parámetros recibidos
        NotificationSearchCriteria criteria = new NotificationSearchCriteria(channel, destination, status, message);

        Page<NotificationDocument> notificationsPage = notificationService.search(criteria, paging);

        Map<String, Object> response = new HashMap<>();
        response.put("content", notificationsPage.getContent());
        response.put("totalElements", notificationsPage.getTotalElements());
        response.put("totalPages", notificationsPage.getTotalPages());
        response.put("size", notificationsPage.getSize());
        response.put("number", notificationsPage.getNumber());

        return ResponseEntity.ok(response);
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
