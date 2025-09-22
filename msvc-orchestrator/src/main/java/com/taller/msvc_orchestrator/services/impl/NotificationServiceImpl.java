package com.taller.msvc_orchestrator.services.impl;

import com.taller.msvc_orchestrator.DTO.NotificationCreateRequest;

import com.taller.msvc_orchestrator.DTO.NotificationDTO;
import com.taller.msvc_orchestrator.DTO.NotificationSearchCriteria;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;


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
        NotificationDocument probe = new NotificationDocument();
        if (criteria.getChannel() != null) probe.setChannel(criteria.getChannel());
        if (criteria.getStatus() != null) probe.setStatus(criteria.getStatus());
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreNullValues();
        Example<NotificationDocument> ex = Example.of(probe, matcher);
        return notificationRepository.findAll(ex, pageable);
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
