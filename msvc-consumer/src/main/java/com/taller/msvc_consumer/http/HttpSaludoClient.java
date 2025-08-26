package com.taller.msvc_consumer.http;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "saludo-service",
        url = "${LB_SALUDO_URL:http://127.0.0.1:80}"
)
public interface HttpSaludoClient {
    @GetMapping("/saludo")
    String greetings(@RequestParam String nombre, @RequestHeader(value = "Authorization") String token);
}
