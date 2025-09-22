package com.taller.msvc_security.Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String eventType; // new-user, login, password-recovery, password-update
    private String userId;
    private String username;
    private String email;
    private String mobileNumber;
    private LocalDateTime timestamp;
    private Map<String, Object> additionalData;
}
