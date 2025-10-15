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

public class UserSteps {

    private Response response;
    private String createdUserId;
    private String authToken;

    @Given("el servicio de usuarios está disponible")
    public void el_servicio_de_usuarios_esta_disponible() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";
    }

    @When("envío una solicitud POST a \\/users\\/register con los datos:")
    public void envio_una_solicitud_post_a_users_register_con_los_datos(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", data.get("username").replace("\"", ""));
        requestBody.put("email", data.get("email").replace("\"", ""));
        requestBody.put("password", data.get("password").replace("\"", ""));
        requestBody.put("firstName", data.get("firstName").replace("\"", ""));
        requestBody.put("lastName", data.get("lastName").replace("\"", ""));
        requestBody.put("mobileNumber", data.get("mobileNumber").replace("\"", ""));

        response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/users");

        if (response.getStatusCode() == 201) {
            createdUserId = response.jsonPath().getString("id");
        }
    }

    @Then("recibo una respuesta {int} y el usuario es creado con el username {string}")
    public void recibo_una_respuesta_y_el_usuario_es_creado_con_el_username(Integer statusCode, String expectedUsername) {
        assertEquals(statusCode.intValue(), response.getStatusCode());

        if (statusCode == 201) {
            // Validar la estructura de respuesta con JSON Schema
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath("schema/user_schema.json"));

            // Validar que el username coincida
            String actualUsername = response.jsonPath().getString("username");
            assertEquals(expectedUsername, actualUsername);

            // Validar que los campos obligatorios estén presentes
            assertNotNull(response.jsonPath().getString("id"));
            assertNotNull(response.jsonPath().getString("email"));
        }
    }

    @Given("existe un usuario con username {string} y password {string}")
    public void existe_un_usuario_con_username_y_password(String username, String password) {
        // Primero verificar si el servicio está disponible
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";

        // Intentar crear el usuario si no existe (esto es idempotente para las pruebas)
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", username + "@test.com");
        userData.put("password", password);
        userData.put("firstName", "Test");
        userData.put("lastName", "User");
        userData.put("mobileNumber", "3140000000");

        Response registrationResponse = given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/users");

        // Guardamos el ID si se creó exitosamente
        if (registrationResponse.getStatusCode() == 201) {
            createdUserId = registrationResponse.jsonPath().getString("id");
        }
    }

    @When("envío una solicitud POST a \\/users\\/login con:")
    public void envio_una_solicitud_post_a_users_login_con(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", data.get("username").replace("\"", ""));
        loginData.put("password", data.get("password").replace("\"", ""));

        response = given()
                .contentType("application/json")
                .body(loginData)
                .when()
                .post("/auth/login");

        if (response.getStatusCode() == 200) {
            authToken = response.jsonPath().getString("token");
        }
    }

    @Then("recibo un token JWT válido en la respuesta y código {int}")
    public void recibo_un_token_jwt_valido_en_la_respuesta_y_codigo(Integer statusCode) {
        assertEquals(statusCode.intValue(), response.getStatusCode());

        if (statusCode == 200) {
            // Validar la estructura de respuesta con JSON Schema
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath("schema/auth_response_schema.json"));

            // Verificar que el token existe
            assertNotNull("El token no debe ser nulo", authToken);
            assertThat("El token no debe estar vacío", authToken, not(emptyString()));

            // Verificar que la respuesta contiene los campos esperados
            assertNotNull(response.jsonPath().getString("token"));

            // Opcional: validar estructura básica del JWT (formato xxx.yyy.zzz)
            String[] jwtParts = authToken.split("\\.");
            assertEquals("El JWT debe tener 3 partes separadas por punto", 3, jwtParts.length);
        }
    }

    @Given("existe un usuario con email {string}")
    public void existe_un_usuario_con_email(String email) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";

        // Crear usuario de prueba
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "testuser_" + System.currentTimeMillis());
        userData.put("email", email);
        userData.put("password", "TestPassword123");
        userData.put("firstName", "Test");
        userData.put("lastName", "User");
        userData.put("mobileNumber", "3140000000");

        Response registrationResponse = given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/users");

        if (registrationResponse.getStatusCode() == 201) {
            createdUserId = registrationResponse.jsonPath().getString("id");
        }
    }

    @When("envío una solicitud POST a \\/users\\/password-recovery con:")
    public void envio_una_solicitud_post_a_users_password_recovery_con(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        Map<String, String> recoveryData = new HashMap<>();
        recoveryData.put("email", data.get("email").replace("\"", ""));

        response = given()
                .contentType("application/json")
                .body(recoveryData)
                .when()
                .post("/auth/tokens");
    }

    @Then("recibo una respuesta {int} y se envía el evento de recuperación de contraseña")
    public void recibo_una_respuesta_y_se_envia_el_evento_de_recuperacion_de_contrasena(Integer statusCode) {
        assertEquals(statusCode.intValue(), response.getStatusCode());

        if (statusCode == 200) {
            // Verificar que la respuesta contiene un mensaje de éxito
            assertNotNull(response.getBody());
        }
    }

    @Given("existe un usuario con id {string} y username {string}")
    public void existe_un_usuario_con_id_y_username(String id, String username) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";

        // Crear usuario y obtener su ID real
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", username + "@test.com");
        userData.put("password", "TestPassword123");
        userData.put("firstName", "Juan");
        userData.put("lastName", "Pérez");
        userData.put("mobileNumber", "3140000000");

        Response registrationResponse = given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/users");

        if (registrationResponse.getStatusCode() == 201) {
            createdUserId = registrationResponse.jsonPath().getString("id");

            // Login para obtener token
            Map<String, String> loginData = new HashMap<>();
            loginData.put("username", username);
            loginData.put("password", "TestPassword123");

            Response loginResponse = given()
                    .contentType("application/json")
                    .body(loginData)
                    .when()
                    .post("/auth/login");

            if (loginResponse.getStatusCode() == 200) {
                authToken = loginResponse.jsonPath().getString("token");
            }
        }
    }

    @When("envío una solicitud PUT a \\/users\\/{int} con:")
    public void envio_una_solicitud_put_a_users_con(Integer userId, io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        Map<String, String> updateData = new HashMap<>();
        updateData.put("firstName", data.get("firstName").replace("\"", ""));

        // Usar el ID real del usuario creado en el paso anterior
        response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(updateData)
                .when()
                .put("/users/" + createdUserId);
    }

    @Then("recibo una respuesta {int} y el nombre del usuario se actualiza a {string}")
    public void recibo_una_respuesta_y_el_nombre_del_usuario_se_actualiza_a(Integer statusCode, String expectedName) {
        assertEquals(statusCode.intValue(), response.getStatusCode());

        if (statusCode == 200) {
            // Validar la estructura de respuesta con JSON Schema
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath("schema/user_schema.json"));

            // Validar que el nombre se actualizó correctamente
            String actualFirstName = response.jsonPath().getString("firstName");
            assertEquals(expectedName, actualFirstName);
        }
    }
}
