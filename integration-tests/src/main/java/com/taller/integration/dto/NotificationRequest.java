package com.taller.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String channel;
    private String destination;
    private String templateId;
    private Map<String, Object> parameters;
    private String message;
}
