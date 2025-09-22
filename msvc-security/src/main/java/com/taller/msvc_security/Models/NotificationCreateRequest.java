package com.taller.msvc_security.Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateRequest {
    private String channel;
    private String destination;
    private String subject;
    private String body;
    private String templateType;
    private Map<String, Object> data;
    private Instant sendAt;
}
