package com.taller.integration.client;

import com.taller.integration.config.IntegrationTestConfig;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
@RequiredArgsConstructor
public class ConsumerServiceClient {

    private final IntegrationTestConfig config;

    public Response consumeApps(String nameForGreeting) {
        return given()
                .baseUri(config.getConsumerBaseUrl())
                .pathParam("nameForGreeting", nameForGreeting)
                .when()
                .get("/consumeApps/{nameForGreeting}")
                .then()
                .extract()
                .response();
    }

    public Response healthCheck() {
        return given()
                .baseUri(config.getConsumerBaseUrl())
                .when()
                .get("/api/health")
                .then()
                .extract()
                .response();
    }
}

