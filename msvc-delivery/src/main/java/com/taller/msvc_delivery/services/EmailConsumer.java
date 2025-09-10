package com.taller.msvc_delivery.services;

import com.taller.msvc_delivery.DTO.NotificationDTO;

public interface EmailConsumer {
     void processEmail(NotificationDTO notification);
}
