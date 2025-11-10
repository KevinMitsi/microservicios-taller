package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
@RequiredArgsConstructor
public class SaludoServiceClient {

    private final IntegrationTestConfig config;

    public Response getGreeting(String nombre, String token) {
        return given()
                .baseUri(config.getSaludoBaseUrl())
                .header("Authorization", token)
                .queryParam("nombre", nombre)
                .when()
                .get("/saludo")
                .then()
                .extract()
                .response();
    }

    public Response healthCheck() {
        return given()
                .baseUri(config.getSaludoBaseUrl())
                .when()
                .get("/health")
                .then()
                .extract()
                .response();
    }
}

