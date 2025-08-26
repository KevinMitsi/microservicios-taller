package com.taller.msvc_consumer.service.impl;

import com.taller.msvc_consumer.DTO.LoginRequestDto;
import com.taller.msvc_consumer.DTO.LoginResponseDto;
import com.taller.msvc_consumer.http.HttpSaludoClient;
import com.taller.msvc_consumer.http.HttpSecurityClient;
import com.taller.msvc_consumer.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final HttpSecurityClient httpSecurityClient;
    private final HttpSaludoClient httpSaludoClient;

    @Override
    public String getGreeting(String nombre, String token) {
        return httpSaludoClient.greetings(nombre, token);
    }

    @Override
    public LoginResponseDto getToken(LoginRequestDto loginRequestDto) {
        return httpSecurityClient.login(loginRequestDto);
    }

}
