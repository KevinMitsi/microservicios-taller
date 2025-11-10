package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import com.taller.integration.dto.NotificationRequest;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrchestratorServiceClient {

    private final IntegrationTestConfig config;

    public Response createNotification(NotificationRequest request, String token) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response getNotifications(String token, int page, int size) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response getNotificationById(String token, String notificationId) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response getChannels(String token) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response getTemplates(String token) {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }

    public Response healthCheck() {
        throw new UnsupportedOperationException("Stub en main; las pruebas usan la implementación en src/test que depende de RestAssured.");
    }
}
