package com.taller.msvc_delivery.services;

import com.taller.msvc_delivery.DTO.NotificationDTO;

public interface SmsConsumer {
    void processSms(NotificationDTO notification);
}
