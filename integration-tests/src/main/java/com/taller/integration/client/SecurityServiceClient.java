package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import com.taller.integration.dto.LoginRequest;
import com.taller.integration.dto.UserRegistrationRequest;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityServiceClient {

    private final IntegrationTestConfig config;

    public Response registerUser(UserRegistrationRequest request) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response login(LoginRequest request) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response getUsers(String token, int page, int size) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response getUserById(String token, String userId) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response healthCheck() {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }
}
