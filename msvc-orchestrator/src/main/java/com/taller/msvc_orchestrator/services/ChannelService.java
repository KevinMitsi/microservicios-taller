package com.taller.msvc_orchestrator.services;

import com.taller.msvc_orchestrator.entities.ChannelEntity;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    List<ChannelEntity> getAvailableChannels();
    Optional<ChannelEntity> getChannel(String key);
}
