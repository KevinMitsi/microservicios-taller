package com.taller.msvc_orchestrator.controllers;

import com.taller.msvc_orchestrator.DTO.NotificationCreateRequest;
import com.taller.msvc_orchestrator.DTO.NotificationSearchCriteria;
import com.taller.msvc_orchestrator.entities.ChannelEntity;
import com.taller.msvc_orchestrator.entities.NotificationDocument;
import com.taller.msvc_orchestrator.services.ChannelService;
import com.taller.msvc_orchestrator.services.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ChannelService channelService;

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

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @Parameter(
                    name = "page",
                    in = ParameterIn.QUERY,
                    description = "Número de página",
                    schema = @Schema(type = "integer", defaultValue = "0")
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    name = "size",
                    in = ParameterIn.QUERY,
                    description = "Tamaño de página",
                    schema = @Schema(type = "integer", defaultValue = "10")
            )
            @RequestParam(defaultValue = "10") int size,
            @Parameter(
                    name = "filter",
                    in = ParameterIn.QUERY,
                    description = "Filtro de búsqueda (opcional, coincidencia parcial)",
                    schema = @Schema(type = "string")
            )
            @RequestParam(required = false) String filter) {

        try {
            Pageable paging = PageRequest.of(page, size);
            Page<NotificationDocument> notificationsPage = notificationService.search(filter, paging);

            Map<String, Object> response = new HashMap<>();
            response.put("content", notificationsPage.getContent());
            response.put("totalElements", notificationsPage.getTotalElements());
            response.put("totalPages", notificationsPage.getTotalPages());
            response.put("size", notificationsPage.getSize());
            response.put("number", notificationsPage.getNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar notificaciones", e);
        }
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
