package com.taller.msvc_delivery.DTO;

import com.taller.msvc_delivery.entities.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSearchCriteria {

    private String channel;
    private String destination;
    private NotificationStatus status;
    private String message;
}