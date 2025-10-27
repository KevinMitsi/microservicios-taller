package com.taller.msvc_security.Services;

import com.taller.msvc_security.Models.NotificationCreateRequest;

public interface NotificationService {
    void sendNotification(NotificationCreateRequest notificationRequest);
}