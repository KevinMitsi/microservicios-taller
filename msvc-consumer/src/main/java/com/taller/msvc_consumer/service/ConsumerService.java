package com.taller.msvc_consumer.service;

import com.taller.msvc_consumer.DTO.LoginRequestDto;
import com.taller.msvc_consumer.DTO.LoginResponseDto;



public interface ConsumerService {
     String getGreeting(String nombre, String token);
     LoginResponseDto getToken(LoginRequestDto loginRequestDto);
}
