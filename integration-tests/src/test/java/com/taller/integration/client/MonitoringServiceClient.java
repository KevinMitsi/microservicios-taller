package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
@RequiredArgsConstructor
public class MonitoringServiceClient {

    private final IntegrationTestConfig config;

    public Response getServiceStatus() {
        return given()
                .baseUri(config.getMonitoringBaseUrl())
                .when()
                .get("/status")
                .then()
                .extract()
                .response();
    }

    public Response healthCheck() {
        return given()
                .baseUri(config.getMonitoringBaseUrl())
                .when()
                .get("/health")
                .then()
                .extract()
                .response();
    }
}

