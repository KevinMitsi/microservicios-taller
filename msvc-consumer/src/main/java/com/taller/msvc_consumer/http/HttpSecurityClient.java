package com.taller.msvc_consumer.http;

import com.taller.msvc_consumer.DTO.LoginRequestDto;
import com.taller.msvc_consumer.DTO.LoginResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "security-service",
        url = "${LB_SECURITY_URL:http://127.0.0.1:8080}"
)
public interface HttpSecurityClient {
    @PostMapping("/login")
    LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto);
}
