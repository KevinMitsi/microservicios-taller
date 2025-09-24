package com.taller.msvc_security.Services.Implementation;

import com.taller.msvc_security.Models.NotificationCreateRequest;
import com.taller.msvc_security.Services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    public static final String NOTIFICATIONS_EXCHANGE = "exchange.notifications";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(NotificationCreateRequest notificationRequest) {
        if (notificationRequest.getDestination() == null || notificationRequest.getDestination().isBlank()) {
            log.warn("No se envía notificación por {}: destino vacío", notificationRequest.getChannel());
            return;
        }

        try {
            rabbitTemplate.convertAndSend(
                NOTIFICATIONS_EXCHANGE,
                notificationRequest.getChannel(),
                notificationRequest
            );
            log.info("Notificación enviada a RabbitMQ: type={}, canal={}, destino={}",
                    notificationRequest.getTemplateType(),
                    notificationRequest.getChannel(),
                    notificationRequest.getDestination());
        } catch (Exception e) {
            log.error("Error al enviar notificación a RabbitMQ (type={}, canal={}, destino={}): {}",
                    notificationRequest.getTemplateType(),
                    notificationRequest.getChannel(),
                    notificationRequest.getDestination(),
                    e.getMessage());
        }
    }
}
