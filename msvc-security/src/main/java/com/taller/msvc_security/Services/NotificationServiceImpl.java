package com.taller.msvc_security.Services;

import com.taller.msvc_security.Models.NotificationCreateRequest;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendNotification(NotificationCreateRequest notificationRequest) {
        // Implementación del envío de notificaciones
        System.out.println("Enviando notificación a: " + notificationRequest.getDestination());
        System.out.println("Mensaje: " + notificationRequest.getBody());
        // Aquí puedes agregar la lógica para enviar notificaciones reales, como correo electrónico o mensajes push
    }
}
