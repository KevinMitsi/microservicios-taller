package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import com.taller.integration.dto.NotificationRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
@RequiredArgsConstructor
public class OrchestratorServiceClient {

    private final IntegrationTestConfig config;

    public Response createNotification(NotificationRequest request, String token) {
        return given()
                .baseUri(config.getOrchestratorBaseUrl())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/notifications")
                .then()
                .extract()
                .response();
    }

    public Response getNotifications(String token, int page, int size) {
        return given()
                .baseUri(config.getOrchestratorBaseUrl())
                .header("Authorization", "Bearer " + token)
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/api/notifications")
                .then()
                .extract()
                .response();
    }

    public Response getNotificationById(String token, String notificationId) {
        return given()
                .baseUri(config.getOrchestratorBaseUrl())
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/notifications/" + notificationId)
                .then()
                .extract()
                .response();
    }

    public Response getChannels(String token) {
        return given()
                .baseUri(config.getOrchestratorBaseUrl())
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/notifications/channels")
                .then()
                .extract()
                .response();
    }

    public Response getTemplates(String token) {
        return given()
                .baseUri(config.getOrchestratorBaseUrl())
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/notifications/templates")
                .then()
                .extract()
                .response();
    }

    public Response healthCheck() {
        return given()
                .baseUri(config.getOrchestratorBaseUrl())
                .when()
                .get("/api/health")
                .then()
                .extract()
                .response();
    }
}

