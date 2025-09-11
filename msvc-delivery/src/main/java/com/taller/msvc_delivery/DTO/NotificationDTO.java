package com.taller.msvc_delivery.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

 @Data
 @NoArgsConstructor
 @AllArgsConstructor
public class NotificationDTO {
    private String notificationId;
    private String channel;
    private String destination;
    private String message;
    private String subject;

}
