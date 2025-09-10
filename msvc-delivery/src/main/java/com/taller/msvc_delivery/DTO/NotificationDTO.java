package com.taller.msvc_delivery.DTO;

public record NotificationDTO(
        String notificationId,
        String channel,
        String destination,
        String message,
        String subject
) {}
