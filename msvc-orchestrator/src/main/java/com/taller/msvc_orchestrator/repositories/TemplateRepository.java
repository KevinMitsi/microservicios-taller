package com.taller.msvc_orchestrator.repositories;

import com.taller.msvc_orchestrator.entities.TemplateEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends MongoRepository<TemplateEntity, String> {
    List<TemplateEntity> findByChannelAndActiveTrue(String channel);
    Optional<TemplateEntity> findByTypeAndChannel(String type, String channel);
    List<TemplateEntity> findAllByActiveTrue();

}