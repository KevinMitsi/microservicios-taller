package com.taller.msvc_orchestrator.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {
    private String templateId;
    private String channel;
    private String destination;
    private String subject;
    private String body;
    private Map<String,Object> data;
}