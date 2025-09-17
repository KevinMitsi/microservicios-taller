package com.taller.msvc_orchestrator.repositories;

import com.taller.msvc_orchestrator.entities.ChannelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends MongoRepository<ChannelEntity, String> {
    Optional<ChannelEntity> findByKey(String key);
    List<ChannelEntity> findByEnabledTrue();
}

