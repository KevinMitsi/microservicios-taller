package com.taller.msvc_orchestrator.services.impl;

import com.taller.msvc_orchestrator.DTO.NotificationCreateRequest;
import com.taller.msvc_orchestrator.DTO.NotificationSearchCriteria;
import com.taller.msvc_orchestrator.DTO.NotificationDTO;
import com.taller.msvc_orchestrator.entities.ChannelEntity;
import com.taller.msvc_orchestrator.entities.NotificationDocument;
import com.taller.msvc_orchestrator.entities.NotificationStatus;
import com.taller.msvc_orchestrator.entities.TemplateEntity;
import com.taller.msvc_orchestrator.repositories.NotificationRepository;

import com.taller.msvc_orchestrator.services.ChannelService;
import com.taller.msvc_orchestrator.services.NotificationService;
import com.taller.msvc_orchestrator.services.TemplateService;
import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    public static final String NOTIFICATIONS_SENDING_METHOD = "exchange.notifications";
    private final NotificationRepository notificationRepository;
    private final TemplateService templateService;
    private final RabbitTemplate rabbitTemplate;
    private final ChannelService channelService;

    @Override
    @Transactional
    public NotificationDocument createAndSend(NotificationCreateRequest notiRequest) {
        // 1) Obtener y validar canal
        ChannelEntity channel = channelService.getChannel(notiRequest.getChannel())
                .orElseThrow(() -> new IllegalArgumentException("Canal no soportado: " + notiRequest.getChannel()));

        if (!Boolean.TRUE.equals(channel.isEnabled())) {
            throw new IllegalStateException("El canal '" + channel.getKey() + "' está deshabilitado.");
        }

        // 2) Construir documento base
        NotificationDocument notification = buildDocumentFromRequest(notiRequest);
        notification.setChannel(channel.getKey());

        // 3) Renderizar template si se envió un tipo
        if (notiRequest.getTemplateType() != null) {
            TemplateEntity template = templateService.getByTypeAndChannel(
                    notiRequest.getTemplateType(),
                    channel.getKey()
            );

            notification.setSubject(template.getSubject());
            notification.setBody(templateService.render(template.getBody(), notiRequest.getData()));
        } else {
            // fallback si no hay templateType
            if (notification.getBody() == null) {
                notification.setBody(notiRequest.getBody());
            }
            if (notification.getSubject() == null) {
                notification.setSubject(notiRequest.getSubject());
            }
        }

        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(Instant.now());

        // 4) Persistir
        NotificationDocument saved = notificationRepository.save(notification);

        // 5) Publicar
        NotificationDTO dto = saved.toDto();
        try {
            rabbitTemplate.convertAndSend(NOTIFICATIONS_SENDING_METHOD, channel.getKey(), dto);
            saved.setStatus(NotificationStatus.SENT);
            saved.setSentAt(Instant.now());
        } catch (Exception e) {
            saved.setStatus(NotificationStatus.FAILED);
        }

        return notificationRepository.save(saved);
    }

    @Override
    public NotificationDocument schedule(NotificationCreateRequest req, Instant sendAt) {
        NotificationDocument doc = buildDocumentFromRequest(req);
        doc.setScheduledAt(sendAt);
        doc.setStatus(NotificationStatus.SCHEDULED);
        doc.setCreatedAt(Instant.now());
        return notificationRepository.save(doc);
    }

    @Override
    public Page<NotificationDocument> search(NotificationSearchCriteria criteria, Pageable pageable) {
        // Crear un documento de ejemplo para filtrado
        NotificationDocument probe = new NotificationDocument();

        // Aplicar filtros específicos
        if (criteria.getChannel() != null && !criteria.getChannel().trim().isEmpty()) {
            probe.setChannel(criteria.getChannel());
        }
        if (criteria.getDestination() != null && !criteria.getDestination().trim().isEmpty()) {
            probe.setDestination(criteria.getDestination());
        }
        if (criteria.getStatus() != null) {
            probe.setStatus(criteria.getStatus());
        }

        // Configurar el matcher para búsqueda flexible
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        Example<NotificationDocument> example = Example.of(probe, matcher);

        // Si hay filtro de mensaje, necesitamos hacer una búsqueda más compleja
        if (criteria.getMessage() != null && !criteria.getMessage().trim().isEmpty()) {
            // Para el filtro de mensaje, haremos una búsqueda manual ya que Spring Data Example
            // no puede buscar en múltiples campos con OR
            return searchWithMessageFilter(criteria, pageable);
        }

        return notificationRepository.findAll(example, pageable);
    }

    private Page<NotificationDocument> searchWithMessageFilter(NotificationSearchCriteria criteria, Pageable pageable) {
        // Obtener todos los documentos sin el filtro de mensaje
        NotificationDocument probe = new NotificationDocument();
        if (criteria.getChannel() != null && !criteria.getChannel().trim().isEmpty()) {
            probe.setChannel(criteria.getChannel());
        }
        if (criteria.getDestination() != null && !criteria.getDestination().trim().isEmpty()) {
            probe.setDestination(criteria.getDestination());
        }
        if (criteria.getStatus() != null) {
            probe.setStatus(criteria.getStatus());
        }

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        Example<NotificationDocument> example = Example.of(probe, matcher);

        // Por simplicidad, obtenemos todos los resultados y filtramos en memoria
        // Para un sistema de producción, sería mejor usar consultas MongoDB nativas
        Page<NotificationDocument> allResults = notificationRepository.findAll(example, pageable);

        String messageFilter = criteria.getMessage().toLowerCase();

        // Filtrar los resultados por mensaje en subject o body
        List<NotificationDocument> filteredContent = allResults.getContent().stream()
                .filter(doc ->
                    (doc.getSubject() != null && doc.getSubject().toLowerCase().contains(messageFilter)) ||
                    (doc.getBody() != null && doc.getBody().toLowerCase().contains(messageFilter))
                )
                .collect(Collectors.toList());

        // Crear una nueva página con los resultados filtrados
        return new PageImpl<>(
                filteredContent,
                pageable,
                filteredContent.size()
        );
    }

    @Override
    public Optional<NotificationDocument> findById(String id) {
        return notificationRepository.findById(id);
    }

    @Override
    public void cancel(String id) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getStatus() == NotificationStatus.SCHEDULED || n.getStatus() == NotificationStatus.PENDING) {
                n.setStatus(NotificationStatus.CANCELLED);
                notificationRepository.save(n);
            } else {
                throw new IllegalStateException("No se puede cancelar en estado " + n.getStatus());
            }
        });
    }

    private NotificationDocument buildDocumentFromRequest(NotificationCreateRequest request) {
        NotificationDocument notification = new NotificationDocument();
        notification.setChannel(request.getChannel());
        notification.setDestination(request.getDestination());
        notification.setData(request.getData());
        notification.setSubject(request.getSubject());
        notification.setBody(request.getBody());
        return notification;
    }
}
