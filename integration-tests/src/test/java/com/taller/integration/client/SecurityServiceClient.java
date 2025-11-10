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
        return given()
                .baseUri(config.getSecurityBaseUrl())
                .when()
                .get("/api/health")
                .then()
                .extract()
                .response();
    }
}

