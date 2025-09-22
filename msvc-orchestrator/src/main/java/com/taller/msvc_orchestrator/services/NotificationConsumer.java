package com.taller.msvc_orchestrator.services;

import com.taller.msvc_orchestrator.DTO.NotificationCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "queue.email")
    public void handleEmailNotification(NotificationCreateRequest notificationRequest) {
        log.info("Recibida notificación de email desde msvc-security: templateType={}, destino={}",
                notificationRequest.getTemplateType(),
                notificationRequest.getDestination());

        try {
            // Procesar la notificación usando el servicio del orquestador
            notificationService.createAndSend(notificationRequest);
            log.info("Notificación de email procesada exitosamente para destino: {}",
                    notificationRequest.getDestination());
        } catch (Exception e) {
            log.error("Error al procesar notificación de email: {}", e.getMessage(), e);
            throw e; // Re-throw para que RabbitMQ maneje el retry
        }
    }

    @RabbitListener(queues = "queue.sms")
    public void handleSmsNotification(NotificationCreateRequest notificationRequest) {
        log.info("Recibida notificación de SMS desde msvc-security: templateType={}, destino={}",
                notificationRequest.getTemplateType(),
                notificationRequest.getDestination());

        try {
            // Procesar la notificación usando el servicio del orquestador
            notificationService.createAndSend(notificationRequest);
            log.info("Notificación de SMS procesada exitosamente para destino: {}",
                    notificationRequest.getDestination());
        } catch (Exception e) {
            log.error("Error al procesar notificación de SMS: {}", e.getMessage(), e);
            throw e; // Re-throw para que RabbitMQ maneje el retry
        }
    }
}
