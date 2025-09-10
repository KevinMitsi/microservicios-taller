package com.taller.msvc_delivery.services;

import com.taller.msvc_delivery.entities.ChannelEntity;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    List<ChannelEntity> getAvailableChannels();
    Optional<ChannelEntity> getChannel(String key);
}
