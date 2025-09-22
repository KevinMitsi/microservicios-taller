package com.taller.msvc_security.Services;

import com.taller.msvc_security.Models.UserEvent;

public interface UserEventService {
    void publishEvent(UserEvent event);
}
