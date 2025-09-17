package com.taller.msvc_orchestrator.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationScheduleRequest {
    private NotificationCreateRequest create;
    private Instant scheduledAt;
}