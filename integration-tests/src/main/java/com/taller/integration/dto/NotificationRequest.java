package com.taller.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String channel;
    private String destination;
    private String templateId;
    private Map<String, Object> parameters;
    private String message;
}
package com.taller.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}

