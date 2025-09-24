package com.taller.msvc_orchestrator.services;

import com.taller.msvc_orchestrator.DTO.NotificationCreateRequest;
import com.taller.msvc_orchestrator.DTO.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    public static final String EMAIL_CHANEL_STRING = "email";
    public static final String SMS_CHANNEL_STRING = "sms";
    private final NotificationService notificationService;

    @RabbitListener(queues = "queue.new-user")
    public void handleNewUserEvent(UserEvent userEvent) {
        log.info("Recibido evento new-user: userId={}, username={}",
                userEvent.getUserId(), userEvent.getUsername());

        try {
            // Enviar notificación de bienvenida por email
            if (userEvent.getEmail() != null && !userEvent.getEmail().isBlank()) {
                sendNotification("new-user", EMAIL_CHANEL_STRING, userEvent);
            }

            // Enviar notificación de bienvenida por SMS
            if (userEvent.getMobileNumber() != null && !userEvent.getMobileNumber().isBlank()) {
                sendNotification("new-user", SMS_CHANNEL_STRING
                        , userEvent);
            }

            log.info("Notificaciones de nuevo usuario enviadas para: {}", userEvent.getUsername());
        } catch (Exception e) {
            log.error("Error procesando evento new-user: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "queue.login")
    public void handleLoginEvent(UserEvent userEvent) {
        log.info("Recibido evento login: userId={}, username={}",
                userEvent.getUserId(), userEvent.getUsername());

        try {
            // Enviar notificación de login por email
            if (userEvent.getEmail() != null && !userEvent.getEmail().isBlank()) {
                sendNotification("login", EMAIL_CHANEL_STRING, userEvent);
            }

            log.info("Notificación de login enviada para: {}", userEvent.getUsername());
        } catch (Exception e) {
            log.error("Error procesando evento login: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "queue.password-recovery")
    public void handlePasswordRecoveryEvent(UserEvent userEvent) {
        log.info("Recibido evento password-recovery: userId={}, username={}",
                userEvent.getUserId(), userEvent.getUsername());

        try {
            // Enviar notificación de recuperación por email
            if (userEvent.getEmail() != null && !userEvent.getEmail().isBlank()) {
                sendNotification("password-recovery", EMAIL_CHANEL_STRING, userEvent);
            }

            // Enviar notificación de recuperación por SMS
            if (userEvent.getMobileNumber() != null && !userEvent.getMobileNumber().isBlank()) {
                sendNotification("password-recovery", SMS_CHANNEL_STRING, userEvent);
            }

            log.info("Notificaciones de recuperación de contraseña enviadas para: {}", userEvent.getUsername());
        } catch (Exception e) {
            log.error("Error procesando evento password-recovery: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "queue.password-update")
    public void handlePasswordUpdateEvent(UserEvent userEvent) {
        log.info("Recibido evento password-update: userId={}, username={}",
                userEvent.getUserId(), userEvent.getUsername());

        try {
            // Enviar notificación de confirmación por email
            if (userEvent.getEmail() != null && !userEvent.getEmail().isBlank()) {
                sendNotification("password-update", EMAIL_CHANEL_STRING, userEvent);
            }

            log.info("Notificación de actualización de contraseña enviada para: {}", userEvent.getUsername());
        } catch (Exception e) {
            log.error("Error procesando evento password-update: {}", e.getMessage(), e);
        }
    }

    private void sendNotification(String templateType, String channel, UserEvent userEvent) {
        NotificationCreateRequest notificationRequest = new NotificationCreateRequest();
        notificationRequest.setTemplateType(templateType);
        notificationRequest.setChannel(channel);
        notificationRequest.setDestination(channel.equals(EMAIL_CHANEL_STRING) ? userEvent.getEmail() : userEvent.getMobileNumber());

        // Preparar datos para el template
        Map<String, Object> data = new HashMap<>();
        data.put("username", userEvent.getUsername());
        data.putAll(userEvent.getAdditionalData() != null ? userEvent.getAdditionalData() : new HashMap<>());

        notificationRequest.setData(data);

        // Enviar la notificación usando el servicio del orquestador
        notificationService.createAndSend(notificationRequest);
    }
}
