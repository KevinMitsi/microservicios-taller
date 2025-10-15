package com.taller.msvc_security.stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class NotificationSteps {

    private Response response;

    @Given("el servicio de notificaciones está disponible")
    public void el_servicio_de_notificaciones_esta_disponible() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";
    }

    @When("envío una solicitud POST a \\/notifications\\/send con:")
    public void envio_una_solicitud_post_a_notifications_send_con(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("channel", data.get("channel").replace("\"", ""));
        requestBody.put("destination", data.get("destination").replace("\"", ""));
        requestBody.put("templateType", data.get("templateType").replace("\"", ""));
        requestBody.put("data", data.get("data").replace("\"", ""));

        response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/notifications/send");
    }

    @Then("recibo una respuesta {int} y la notificación es enviada correctamente")
    public void recibo_una_respuesta_y_la_notificacion_es_enviada_correctamente(Integer statusCode) {
        assertEquals(statusCode.intValue(), response.getStatusCode());

        if (statusCode == 200) {
            // Validar la estructura de respuesta con JSON Schema
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath("schema/notification_response_schema.json"));

            // Verificar que el status sea "SENT" o similar
            String status = response.jsonPath().getString("status");
            assertThat(status, anyOf(equalTo("SENT"), equalTo("SUCCESS"), equalTo("DELIVERED")));
        }
    }

    @Then("recibo una respuesta {int} y la notificación \\*\\*no\\*\\* es enviada")
    public void recibo_una_respuesta_y_la_notificacion_no_es_enviada(Integer statusCode) {
        assertEquals(statusCode.intValue(), response.getStatusCode());

        if (statusCode == 400) {
            // Validar la estructura de respuesta de error con JSON Schema
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath("schema/error_response_schema.json"));

            // Verificar que exista un mensaje de error
            assertNotNull(response.jsonPath().getString("message"));
            assertNotNull(response.jsonPath().getString("error"));
        }
    }
}
