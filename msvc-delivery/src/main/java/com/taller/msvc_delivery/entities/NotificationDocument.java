package com.taller.msvc_delivery.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "notifications")
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDocument {
    @Id
    private String id;
    private String templateId;
    private String channel;
    private String destination;
    private Map<String, Object> data;
    private String subject;
    private String body;
    private NotificationStatus status;
    private Instant scheduledAt;
    private Instant createdAt;
    private Instant sentAt;
    private String errorMessage;

}