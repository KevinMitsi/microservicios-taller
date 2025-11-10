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
        // Primero intentar con /smoke (específico para tests)
        try {
            Response response = given()
                    .baseUri(config.getSaludoBaseUrl())
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
                    .baseUri(config.getSaludoBaseUrl())
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
                .baseUri(config.getSaludoBaseUrl())
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
                    .baseUri(config.getSaludoBaseUrl())
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
                    .baseUri(config.getSaludoBaseUrl())
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
                    .baseUri(config.getSaludoBaseUrl())
                    .when()
                    .get("/health")
                    .then()
                    .extract()
                    .response();
        }
    }
}

