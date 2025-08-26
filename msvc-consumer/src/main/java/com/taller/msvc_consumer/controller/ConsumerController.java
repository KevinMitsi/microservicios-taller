package com.taller.msvc_consumer.controller;

import com.taller.msvc_consumer.DTO.LoginRequestDto;
import com.taller.msvc_consumer.DTO.LoginResponseDto;
import com.taller.msvc_consumer.service.ConsumerService;
import com.taller.msvc_consumer.service.RandomStringService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConsumerController {
    private final RandomStringService randomStringService;
    private final ConsumerService consumerService;

    @GetMapping("/consumeApps")
    public String askForToken() {
        LoginRequestDto securityRequest = new LoginRequestDto(randomStringService.generateRandomString(15)
                , randomStringService.generateRandomString(10));
         LoginResponseDto securityResponse = consumerService.getToken(securityRequest);
        System.out.println("Token received: " + securityResponse.getToken());
         return consumerService.getGreeting("ConsumerApp", "Bearer "+securityResponse.getToken());
    }
}
