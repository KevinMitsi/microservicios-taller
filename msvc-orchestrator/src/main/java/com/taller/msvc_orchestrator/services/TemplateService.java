package com.taller.msvc_orchestrator.services;

import com.taller.msvc_orchestrator.entities.TemplateEntity;

import java.util.Map;

public interface TemplateService {
    TemplateEntity getByTypeAndChannel(String type, String channel);
    String render(String templateBody, Map<String, Object> data);
}
