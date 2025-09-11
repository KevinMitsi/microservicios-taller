package com.taller.msvc_delivery.repositories;

import com.taller.msvc_delivery.entities.TemplateEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TemplateRepository extends MongoRepository<TemplateEntity, String> {
    List<TemplateEntity> findByChannelAndActiveTrue(String channel);
}