package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaludoServiceClient {

    private final IntegrationTestConfig config;

    public Response getGreeting(String nombre, String token) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response healthCheck() {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }
}
