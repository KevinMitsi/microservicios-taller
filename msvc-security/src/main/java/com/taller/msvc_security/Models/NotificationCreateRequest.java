package com.taller.msvc_security.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {
    private String templateType;
    private String channel;
    private String destination;
    private String subject;
    private String body;
    private Map<String,Object> data;
}