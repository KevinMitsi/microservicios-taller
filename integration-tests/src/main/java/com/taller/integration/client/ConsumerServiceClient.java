package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumerServiceClient {

    private final IntegrationTestConfig config;

    // Implementación stub para production/main module — las pruebas usan la clase en src/test
    public Response consumeApps(String nameForGreeting) {
        throw new UnsupportedOperationException("Este cliente es solo un stub en el código main. Use la clase de pruebas en src/test para integración.");
    }

    public Response healthCheck() {
        throw new UnsupportedOperationException("Este cliente es solo un stub en el código main. Use la clase de pruebas en src/test para integración.");
    }
}
