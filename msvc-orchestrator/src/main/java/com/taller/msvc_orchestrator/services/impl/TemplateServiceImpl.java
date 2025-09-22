package com.taller.msvc_orchestrator.services.impl;

import com.taller.msvc_orchestrator.entities.TemplateEntity;
import com.taller.msvc_orchestrator.repositories.TemplateRepository;
import com.taller.msvc_orchestrator.services.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final TemplateRepository templateRepository;

    @Override
    public TemplateEntity getByTypeAndChannel(String type, String channel) {
        return templateRepository.findByTypeAndChannel(type, channel)
                .orElseThrow(() -> new NoSuchElementException("Template no existe"));
    }

    @Override
    public String render(String templateBody, Map<String, Object> data) {
        String result = templateBody;
        if (data != null) {
            for (Map.Entry<String,Object> e : data.entrySet()) {
                result = result.replace("{{" + e.getKey() + "}}", Objects.toString(e.getValue(), ""));
            }
        }
        return result;
    }
}
