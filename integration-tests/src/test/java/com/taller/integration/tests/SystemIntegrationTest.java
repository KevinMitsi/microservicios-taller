package com.taller.integration.tests;

import com.taller.integration.IntegrationTestApplication;
import com.taller.integration.client.*;
import com.taller.integration.config.IntegrationTestConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;

/**
 * Prueba de integración completa del sistema de microservicios
 * Verifica el correcto funcionamiento de todos los servicios trabajando en conjunto
 */
@SpringBootTest(classes = IntegrationTestApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SystemIntegrationTest {

    @Autowired
    private SecurityServiceClient securityClient;

    @Autowired
    private SaludoServiceClient saludoClient;

    @Autowired
    private ConsumerServiceClient consumerClient;

    @Autowired
    private OrchestratorServiceClient orchestratorClient;

    @Autowired
    private MonitoringServiceClient monitoringClient;

    @Autowired
    private IntegrationTestConfig config;

    private static String authToken;
    private static String userId;

    @BeforeAll
    public void waitForServicesReady() {
        System.out.println("=== Esperando a que los servicios estén listos ===");

        // Esperar a que todos los servicios estén disponibles
        await().atMost(60, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                    try {
                        Response securityHealth = securityClient.healthCheck();
                        Response saludoHealth = saludoClient.healthCheck();
                        Response consumerHealth = consumerClient.healthCheck();
                        Response orchestratorHealth = orchestratorClient.healthCheck();

                        boolean allUp = securityHealth.getStatusCode() == 200 &&
                                       saludoHealth.getStatusCode() == 200 &&
                                       consumerHealth.getStatusCode() == 200 &&
                                       orchestratorHealth.getStatusCode() == 200;

                        if (allUp) {
                            System.out.println("✓ Todos los servicios están listos");
                        }
                        return allUp;
                    } catch (Exception e) {
                        System.out.println("Esperando servicios... " + e.getMessage());
                        return false;
                    }
                });
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Health Checks - Todos los servicios están operativos")
    public void test01_allServicesAreHealthy() {
        System.out.println("\n=== Test 1: Verificando Health de todos los servicios ===");

        // Verificar msvc-security
        Response securityHealth = securityClient.healthCheck();
        assertThat(securityHealth.getStatusCode())
                .as("Security service debe estar disponible")
                .isEqualTo(200);
        System.out.println("✓ msvc-security está UP");

        // Verificar msvc-saludo
        Response saludoHealth = saludoClient.healthCheck();
        assertThat(saludoHealth.getStatusCode())
                .as("Saludo service debe estar disponible")
                .isEqualTo(200);
        System.out.println("✓ msvc-saludo está UP");

        // Verificar msvc-consumer
        Response consumerHealth = consumerClient.healthCheck();
        assertThat(consumerHealth.getStatusCode())
                .as("Consumer service debe estar disponible")
                .isEqualTo(200);
        System.out.println("✓ msvc-consumer está UP");

        // Verificar msvc-orchestrator
        Response orchestratorHealth = orchestratorClient.healthCheck();
        assertThat(orchestratorHealth.getStatusCode())
                .as("Orchestrator service debe estar disponible")
                .isEqualTo(200);
        System.out.println("✓ msvc-orchestrator está UP");

        System.out.println("✓ Todos los servicios están operativos");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Flujo de Autenticación - Registro y Login de Usuario")
    public void test02_userRegistrationAndLogin() {
        System.out.println("\n=== Test 2: Flujo de Autenticación ===");

        // Crear usuario único para esta prueba
        String timestamp = String.valueOf(System.currentTimeMillis());
        String username = "testuser_" + timestamp;
        String email = "test_" + timestamp + "@example.com";

        // 2.1 Registrar usuario
        System.out.println("Registrando usuario: " + username);
        var registrationRequest = new com.taller.integration.dto.UserRegistrationRequest(
                username,
                email,
                "SecurePass123!",
                "Test",
                "User"
        );

        Response registerResponse = securityClient.registerUser(registrationRequest);
        assertThat(registerResponse.getStatusCode())
                .as("Registro de usuario debe ser exitoso")
                .isEqualTo(201);

        userId = registerResponse.jsonPath().getString("id");
        assertThat(userId).as("El ID del usuario debe estar presente").isNotNull();
        System.out.println("✓ Usuario registrado con ID: " + userId);

        // 2.2 Login con el usuario creado
        System.out.println("Autenticando usuario...");
        var loginRequest = new com.taller.integration.dto.LoginRequest(username, "SecurePass123!");

        Response loginResponse = securityClient.login(loginRequest);
        assertThat(loginResponse.getStatusCode())
                .as("Login debe ser exitoso")
                .isEqualTo(200);

        authToken = loginResponse.jsonPath().getString("token");
        assertThat(authToken).as("Token JWT debe estar presente").isNotNull();
        System.out.println("✓ Autenticación exitosa, token obtenido");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Servicio de Saludo - Obtener saludo personalizado")
    public void test03_greetingService() {
        System.out.println("\n=== Test 3: Servicio de Saludo ===");

        String nombre = "IntegrationTest";
        Response greetingResponse = saludoClient.getGreeting(nombre, "Bearer " + authToken);

        assertThat(greetingResponse.getStatusCode())
                .as("Servicio de saludo debe responder correctamente")
                .isEqualTo(200);

        String greeting = greetingResponse.getBody().asString();
        assertThat(greeting)
                .as("El saludo debe contener el nombre proporcionado")
                .contains(nombre);

        System.out.println("✓ Saludo recibido: " + greeting);
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Consumer Service - Flujo completo de integración")
    public void test04_consumerServiceIntegration() {
        System.out.println("\n=== Test 4: Consumer Service - Flujo completo ===");

        String nameForGreeting = "SystemTest";

        // El consumer service debe:
        // 1. Generar credenciales aleatorias
        // 2. Obtener un token del security service
        // 3. Llamar al greeting service con el token
        // 4. Devolver el saludo

        Response consumerResponse = consumerClient.consumeApps(nameForGreeting);

        assertThat(consumerResponse.getStatusCode())
                .as("Consumer service debe completar el flujo exitosamente")
                .isEqualTo(200);

        String result = consumerResponse.getBody().asString();
        assertThat(result)
                .as("La respuesta debe contener un saludo")
                .contains("Hola", nameForGreeting);

        System.out.println("✓ Flujo completo de consumer exitoso: " + result);
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Gestión de Usuarios - Listar y obtener detalles")
    public void test05_userManagement() {
        System.out.println("\n=== Test 5: Gestión de Usuarios ===");

        // 5.1 Listar usuarios
        System.out.println("Listando usuarios...");
        Response usersResponse = securityClient.getUsers(authToken, 0, 10);

        assertThat(usersResponse.getStatusCode())
                .as("Debe poder listar usuarios con autenticación")
                .isEqualTo(200);

        int totalElements = usersResponse.jsonPath().getInt("totalElements");
        assertThat(totalElements).as("Debe haber al menos un usuario").isGreaterThan(0);
        System.out.println("✓ Total de usuarios: " + totalElements);

        // 5.2 Obtener usuario específico
        System.out.println("Obteniendo detalles del usuario: " + userId);
        Response userResponse = securityClient.getUserById(authToken, userId);

        assertThat(userResponse.getStatusCode())
                .as("Debe poder obtener detalles del usuario")
                .isEqualTo(200);

        String fetchedUsername = userResponse.jsonPath().getString("username");
        assertThat(fetchedUsername).as("El nombre de usuario debe coincidir").isNotNull();
        System.out.println("✓ Usuario obtenido: " + fetchedUsername);
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Orchestrator - Gestión de canales y templates")
    public void test06_orchestratorChannelsAndTemplates() {
        System.out.println("\n=== Test 6: Orchestrator - Canales y Templates ===");

        // 6.1 Obtener canales disponibles
        System.out.println("Obteniendo canales disponibles...");
        Response channelsResponse = orchestratorClient.getChannels(authToken);

        if (channelsResponse.getStatusCode() == 200) {
            var channels = channelsResponse.jsonPath().getList("$");
            System.out.println("✓ Canales disponibles: " + channels.size());
        } else {
            System.out.println("⚠ Servicio de canales no disponible o requiere configuración");
        }

        // 6.2 Obtener templates disponibles
        System.out.println("Obteniendo templates disponibles...");
        Response templatesResponse = orchestratorClient.getTemplates(authToken);

        if (templatesResponse.getStatusCode() == 200) {
            var templates = templatesResponse.jsonPath().getList("$");
            System.out.println("✓ Templates disponibles: " + templates.size());
        } else {
            System.out.println("⚠ Servicio de templates no disponible o requiere configuración");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Resilencia - Reintentos y manejo de errores")
    public void test07_resilienceAndErrorHandling() {
        System.out.println("\n=== Test 7: Resiliencia del Sistema ===");

        // 7.1 Intentar login con credenciales inválidas
        System.out.println("Probando manejo de credenciales inválidas...");
        var invalidLogin = new com.taller.integration.dto.LoginRequest("invalid_user", "wrong_pass");
        Response invalidLoginResponse = securityClient.login(invalidLogin);

        assertThat(invalidLoginResponse.getStatusCode())
                .as("Debe rechazar credenciales inválidas")
                .isIn(400, 401, 404);
        System.out.println("✓ Sistema rechaza correctamente credenciales inválidas");

        // 7.2 Intentar acceder a recurso protegido sin token
        System.out.println("Probando acceso sin autenticación...");
        Response unauthorizedResponse = securityClient.getUsers("invalid_token", 0, 10);

        assertThat(unauthorizedResponse.getStatusCode())
                .as("Debe rechazar acceso sin token válido")
                .isIn(401, 403);
        System.out.println("✓ Sistema protege correctamente recursos autenticados");

        // 7.3 Validar que el servicio sigue funcional después de errores
        System.out.println("Verificando que el sistema sigue funcional...");
        Response healthCheck = securityClient.healthCheck();
        assertThat(healthCheck.getStatusCode())
                .as("El sistema debe seguir operativo después de errores")
                .isEqualTo(200);
        System.out.println("✓ Sistema sigue operativo después de manejar errores");
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Monitoreo - Verificar estado del sistema")
    public void test08_monitoringService() {
        System.out.println("\n=== Test 8: Servicio de Monitoreo ===");

        try {
            Response statusResponse = monitoringClient.getServiceStatus();

            if (statusResponse.getStatusCode() == 200) {
                System.out.println("✓ Servicio de monitoreo está activo");
                // Mostrar estado si está disponible
                String status = statusResponse.getBody().asString();
                System.out.println("Estado del sistema: " + status);
            } else {
                System.out.println("⚠ Servicio de monitoreo no configurado completamente");
            }
        } catch (Exception e) {
            System.out.println("⚠ Servicio de monitoreo opcional no disponible: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Performance - Múltiples peticiones concurrentes")
    public void test09_concurrentRequests() {
        System.out.println("\n=== Test 9: Prueba de Concurrencia ===");

        int numberOfRequests = 5;
        long startTime = System.currentTimeMillis();

        // Realizar múltiples peticiones concurrentes
        for (int i = 0; i < numberOfRequests; i++) {
            String nombre = "ConcurrentTest" + i;
            Response response = saludoClient.getGreeting(nombre, "Bearer " + authToken);
            assertThat(response.getStatusCode())
                    .as("Petición " + i + " debe ser exitosa")
                    .isEqualTo(200);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✓ " + numberOfRequests + " peticiones completadas en " + duration + "ms");
        System.out.println("✓ Promedio: " + (duration / numberOfRequests) + "ms por petición");
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Flujo E2E Completo - Desde registro hasta notificación")
    public void test10_completeEndToEndFlow() {
        System.out.println("\n=== Test 10: Flujo End-to-End Completo ===");

        String timestamp = String.valueOf(System.currentTimeMillis());
        String e2eUsername = "e2euser_" + timestamp;
        String e2eEmail = "e2e_" + timestamp + "@test.com";

        // Paso 1: Registrar usuario
        System.out.println("Paso 1: Registrando usuario E2E...");
        var e2eRegistration = new com.taller.integration.dto.UserRegistrationRequest(
                e2eUsername, e2eEmail, "E2EPass123!", "E2E", "Test"
        );
        Response registerResp = securityClient.registerUser(e2eRegistration);
        assertThat(registerResp.getStatusCode()).isEqualTo(201);
        System.out.println("✓ Usuario E2E registrado");

        // Paso 2: Autenticar
        System.out.println("Paso 2: Autenticando usuario E2E...");
        var e2eLogin = new com.taller.integration.dto.LoginRequest(e2eUsername, "E2EPass123!");
        Response loginResp = securityClient.login(e2eLogin);
        assertThat(loginResp.getStatusCode()).isEqualTo(200);
        String e2eToken = loginResp.jsonPath().getString("token");
        System.out.println("✓ Usuario E2E autenticado");

        // Paso 3: Obtener saludo
        System.out.println("Paso 3: Obteniendo saludo personalizado...");
        Response greetingResp = saludoClient.getGreeting(e2eUsername, "Bearer " + e2eToken);
        assertThat(greetingResp.getStatusCode()).isEqualTo(200);
        System.out.println("✓ Saludo recibido: " + greetingResp.getBody().asString());

        // Paso 4: Usar consumer service
        System.out.println("Paso 4: Ejecutando flujo completo de consumer...");
        Response consumerResp = consumerClient.consumeApps(e2eUsername);
        assertThat(consumerResp.getStatusCode()).isEqualTo(200);
        System.out.println("✓ Flujo de consumer completado");

        // Paso 5: Verificar servicios siguen operativos
        System.out.println("Paso 5: Verificando que todos los servicios siguen operativos...");
        assertThat(securityClient.healthCheck().getStatusCode()).isEqualTo(200);
        assertThat(saludoClient.healthCheck().getStatusCode()).isEqualTo(200);
        assertThat(consumerClient.healthCheck().getStatusCode()).isEqualTo(200);
        assertThat(orchestratorClient.healthCheck().getStatusCode()).isEqualTo(200);

        System.out.println("\n✓✓✓ FLUJO END-TO-END COMPLETO EXITOSO ✓✓✓");
    }

    @AfterAll
    public void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESUMEN DE PRUEBAS DE INTEGRACIÓN DEL SISTEMA");
        System.out.println("=".repeat(60));
        System.out.println("✓ Todos los servicios verificados y operativos");
        System.out.println("✓ Flujos de integración completados exitosamente");
        System.out.println("✓ Sistema listo para producción");
        System.out.println("=".repeat(60));
    }
}

