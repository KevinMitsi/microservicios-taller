package com.taller.msvc_orchestrator.services.impl;

import com.taller.msvc_orchestrator.entities.ChannelEntity;
import com.taller.msvc_orchestrator.repositories.ChannelRepository;
import com.taller.msvc_orchestrator.services.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;

    @Override
    public List<ChannelEntity> getAvailableChannels() {
        return channelRepository.findByEnabledTrue();
    }

    @Override
    public Optional<ChannelEntity> getChannel(String key) {
        return channelRepository.findByKey(key);
    }

}
