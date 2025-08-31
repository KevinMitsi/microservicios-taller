package com.taller.msvc_security.Models;

import com.taller.msvc_security.Entities.UserDocument;

public class AuthResponse {
    private String token;
    private Integer expiresIn;
    private String tokenType;
    private UserDocument user;

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserDocument getUser() {
        return user;
    }

    public void setUser(UserDocument user) {
        this.user = user;
    }
}