package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import com.taller.integration.dto.LoginRequest;
import com.taller.integration.dto.UserRegistrationRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
@RequiredArgsConstructor
public class SecurityServiceClient {

    private final IntegrationTestConfig config;

    public Response registerUser(UserRegistrationRequest request) {
        return given()
                .baseUri(config.getSecurityBaseUrl())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/users")
                .then()
                .extract()
                .response();
    }

    public Response login(LoginRequest request) {
        return given()
                .baseUri(config.getSecurityBaseUrl())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/auth/login")
                .then()
                .extract()
                .response();
    }

    public Response getUsers(String token, int page, int size) {
        return given()
                .baseUri(config.getSecurityBaseUrl())
                .header("Authorization", "Bearer " + token)
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/api/users")
                .then()
                .extract()
                .response();
    }

    public Response getUserById(String token, String userId) {
        return given()
                .baseUri(config.getSecurityBaseUrl())
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/users/" + userId)
                .then()
                .extract()
                .response();
    }

    public Response healthCheck() {
        // Primero intentar con /smoke (específico para tests)
        try {
            Response response = given()
                    .baseUri(config.getSecurityBaseUrl())
                    .when()
                    .get("/smoke")
                    .then()
                    .extract()
                    .response();

            if (response.getStatusCode() == 200) {
                return response;
            }
        } catch (Exception e) {
            // Continuar con otros endpoints
        }

        // Intentar con /health personalizado
        try {
            Response response = given()
                    .baseUri(config.getSecurityBaseUrl())
                    .when()
                    .get("/health")
                    .then()
                    .extract()
                    .response();

            if (response.getStatusCode() == 200) {
                return response;
            }
        } catch (Exception e) {
            // Continuar con actuator
        }

        // Si falló, intentar con /actuator/health
        return given()
                .baseUri(config.getSecurityBaseUrl())
                .when()
                .get("/actuator/health")
                .then()
                .extract()
                .response();
    }

    public Response smokeTest() {
        // Probar múltiples endpoints sin autenticación
        try {
            // Intentar con /health primero
            Response response = given()
                    .baseUri(config.getSecurityBaseUrl())
                    .config(io.restassured.config.RestAssuredConfig.newConfig()
                            .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                    .setParam("http.connection.timeout", 5000)
                                    .setParam("http.socket.timeout", 5000)))
                    .when()
                    .get("/health")
                    .then()
                    .extract()
                    .response();

            if (response.getStatusCode() == 200) {
                return response;
            }
        } catch (Exception e) {
            // Continuar con otros endpoints
        }

        // Intentar con /actuator/health
        try {
            return given()
                    .baseUri(config.getSecurityBaseUrl())
                    .config(io.restassured.config.RestAssuredConfig.newConfig()
                            .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                    .setParam("http.connection.timeout", 5000)
                                    .setParam("http.socket.timeout", 5000)))
                    .when()
                    .get("/actuator/health")
                    .then()
                    .extract()
                    .response();
        } catch (Exception e) {
            // Retornar respuesta con error
            return given()
                    .baseUri(config.getSecurityBaseUrl())
                    .when()
                    .get("/health")
                    .then()
                    .extract()
                    .response();
        }
    }
}

