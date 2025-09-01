package com.taller.msvc_security.Models;

import com.taller.msvc_security.Entities.UserDocument;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    // Getters and Setters
    private String token;
    private Integer expiresIn;
    private String tokenType;
    private UserDocument user;

}