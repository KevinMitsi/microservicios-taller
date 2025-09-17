package com.taller.msvc_orchestrator.services;

import com.taller.msvc_orchestrator.entities.TemplateEntity;

import java.util.Map;

public interface TemplateService {
    TemplateEntity getTemplate(String id);
    String render(String templateBody, Map<String, Object> data);
}
