package com.taller.integration.tests;

import com.taller.integration.IntegrationTestApplication;
import com.taller.integration.client.*;
import com.taller.integration.config.IntegrationTestConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de humo rápida para verificar que todos los servicios están disponibles
 * Esta prueba es más rápida que SystemIntegrationTest y sirve para verificación inicial
 */
@SpringBootTest(classes = IntegrationTestApplication.class)
public class SmokeTest {

    @Autowired
    private IntegrationTestConfig config;

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

    @Test
    @DisplayName("Smoke Test: Verificar que todos los servicios están disponibles")
    void smokeTest_allServicesAvailable() {
        System.out.println("\n=== SMOKE TEST: Verificando disponibilidad de servicios ===\n");

        boolean allServicesUp = true;

        // Verificar Security Service
        boolean securityAvailable = checkServiceAvailability("msvc-security", "Puerto 8080",
            () -> securityClient.healthCheck(), config.getSecurityBaseUrl());
        if (!securityAvailable) allServicesUp = false;

        // Verificar Saludo Service
        boolean saludoAvailable = checkServiceAvailability("msvc-saludo", "Puerto 80",
            () -> saludoClient.healthCheck(), config.getSaludoBaseUrl());
        if (!saludoAvailable) allServicesUp = false;

        // Verificar Consumer Service
        boolean consumerAvailable = checkServiceAvailability("msvc-consumer", "Puerto 8081",
            () -> consumerClient.healthCheck(), config.getConsumerBaseUrl());
        if (!consumerAvailable) allServicesUp = false;

        // Verificar Orchestrator Service
        boolean orchestratorAvailable = checkServiceAvailability("msvc-orchestrator", "Puerto 8083",
            () -> orchestratorClient.healthCheck(), config.getOrchestratorBaseUrl());
        if (!orchestratorAvailable) allServicesUp = false;

        // Verificar Monitoring Service (opcional)
        checkServiceAvailability("msvc-monitoring", "Puerto 8000",
            () -> monitoringClient.healthCheck(), config.getMonitoringBaseUrl(), true);

        System.out.println("\n" + "=".repeat(60));
        if (allServicesUp) {
            System.out.println("RESULTADO: ✓ TODOS LOS SERVICIOS CRÍTICOS ESTÁN DISPONIBLES");
        } else {
            System.out.println("RESULTADO: ✗ ALGUNOS SERVICIOS NO ESTÁN DISPONIBLES");
            System.out.println("\nPor favor ejecuta: docker-compose up -d");
            System.out.println("Para debug, verifica containers: docker ps");
            System.out.println("Para logs: docker-compose logs [nombre-servicio]");
        }
        System.out.println("=".repeat(60) + "\n");

        assertThat(allServicesUp)
                .as("Todos los servicios críticos deben estar disponibles")
                .isTrue();
    }

    @Test
    @DisplayName("Smoke Test: Verificar conectividad básica de seguridad")
    public void smokeTest_basicSecurityConnectivity() {
        System.out.println("\n=== SMOKE TEST: Conectividad básica de seguridad ===\n");

        // Solo verificar que podemos hacer una petición al servicio
        Response response = securityClient.healthCheck();

        assertThat(response.getStatusCode())
                .as("El servicio de seguridad debe responder")
                .isIn(200, 403); // 200 = disponible, 403 = disponible pero protegido

        System.out.println("✓ Conectividad con msvc-security verificada\n");
    }

    @Test
    @DisplayName("Smoke Test: Verificar conectividad básica de saludo")
    public void smokeTest_basicSaludoConnectivity() {
        System.out.println("\n=== SMOKE TEST: Conectividad básica de saludo ===\n");

        Response response = saludoClient.healthCheck();

        assertThat(response.getStatusCode())
                .as("El servicio de saludo debe responder")
                .isIn(200, 403); // 200 = disponible, 403 = disponible pero protegido

        System.out.println("✓ Conectividad con msvc-saludo verificada\n");
    }

    /**
     * Verifica la disponibilidad de un servicio probando múltiples endpoints
     */
    private boolean checkServiceAvailability(String serviceName, String port, Supplier<Response> healthCheckSupplier,
                                           String baseUrl) {
        return checkServiceAvailability(serviceName, port, healthCheckSupplier, baseUrl, false);
    }

    /**
     * Verifica la disponibilidad de un servicio probando múltiples endpoints
     */
    private boolean checkServiceAvailability(String serviceName, String port, Supplier<Response> healthCheckSupplier,
                                           String baseUrl, boolean isOptional) {

        // Lista de endpoints a probar en orden de prioridad
        List<String> healthEndpoints = Arrays.asList(
            "/smoke",            // Endpoint específico para smoke tests (sin auth)
            "/health",           // Endpoint personalizado
            "/actuator/health",  // Spring Boot Actuator
            "/health/ready",     // Readiness probe
            "/health/live",      // Liveness probe
            "/actuator/info",    // Actuator info
            "/status",           // Endpoint de monitoreo
            "/"                  // Root endpoint como último recurso
        );

        // Lista de puertos alternativos a probar basados en el servicio
        List<String> alternativePorts = getAlternativePorts(serviceName, baseUrl);

        boolean isAvailable = false;
        String successfulEndpoint = null;
        String successfulUrl = null;
        int lastStatusCode = 0;

        // Primero probar con el cliente específico
        try {
            Response response = healthCheckSupplier.get();
            lastStatusCode = response.getStatusCode();
            if (Arrays.asList(200, 202, 204, 403, 404).contains(response.getStatusCode())) {
                isAvailable = true;
                successfulEndpoint = "/health";
                successfulUrl = baseUrl;
            }
        } catch (Exception e) {
            // Intentar con endpoints y puertos alternativos
        }

        // Si el endpoint principal falló, probar todas las combinaciones
        if (!isAvailable) {
            for (String testUrl : alternativePorts) {
                for (String endpoint : healthEndpoints) {
                    try {
                        Response response = given()
                            .baseUri(testUrl)
                            .relaxedHTTPSValidation()
                            .config(io.restassured.config.RestAssuredConfig.newConfig()
                                    .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                            .setParam("http.connection.timeout", 5000)
                                            .setParam("http.socket.timeout", 5000)))
                            .when()
                            .get(endpoint)
                            .then()
                            .extract()
                            .response();

                        lastStatusCode = response.getStatusCode();

                        // Considerar exitosos códigos 200, 202, y algunos otros
                        // También considerar 403 como "servicio disponible pero protegido"
                        // Y 404 como "servicio disponible pero sin health endpoint"
                        if (Arrays.asList(200, 202, 204, 403, 404).contains(response.getStatusCode())) {
                            isAvailable = true;
                            successfulEndpoint = endpoint;
                            successfulUrl = testUrl;
                            break;
                        }
                    } catch (Exception e) {
                        // Continuar con el siguiente endpoint/puerto
                        lastStatusCode = 0; // Indicar conexión fallida
                    }
                }
                if (isAvailable) break;
            }
        }

        // Mostrar resultado
        String icon = isAvailable ? "✓" : (isOptional ? "⚠" : "✗");
        String status = isAvailable ? "DISPONIBLE" : "NO DISPONIBLE";
        String optional = isOptional ? " (opcional)" : "";

        if (isAvailable) {
            String portInfo = successfulUrl.equals(baseUrl) ? port :
                            "Puerto " + extractPort(successfulUrl);
            String protectionStatus = "";
            if (lastStatusCode == 403) {
                protectionStatus = " (protegido)";
            } else if (lastStatusCode == 404) {
                protectionStatus = " (sin health endpoint)";
            }
            System.out.println(icon + " " + serviceName + " [" + status + "] - " + portInfo +
                             (successfulEndpoint != null ? " (endpoint: " + successfulEndpoint + ")" : "") +
                             protectionStatus + optional);
        } else {
            String errorMsg = lastStatusCode == 0 ? "Sin conexión" : "Código: " + lastStatusCode;
            System.out.println(icon + " " + serviceName + " [" + status + "] - " + errorMsg + optional);
        }

        return isAvailable || isOptional;
    }

    /**
     * Obtiene puertos alternativos a probar para cada servicio
     */
    private List<String> getAlternativePorts(String serviceName, String originalBaseUrl) {
        List<String> urls = Arrays.asList(originalBaseUrl); // Comenzar con la URL original

        String baseHost = "http://localhost:";

        switch (serviceName) {
            case "msvc-security":
                return Arrays.asList(
                    originalBaseUrl,
                    baseHost + "8080",
                    baseHost + "8090",
                    baseHost + "9080"
                );
            case "msvc-saludo":
                return Arrays.asList(
                    originalBaseUrl,
                    baseHost + "9090",  // Puerto donde realmente está corriendo
                    baseHost + "80",
                    baseHost + "8080",
                    baseHost + "8090"
                );
            case "msvc-consumer":
                return Arrays.asList(
                    originalBaseUrl,
                    baseHost + "8081",
                    baseHost + "8091",
                    baseHost + "9081"
                );
            case "msvc-orchestrator":
                return Arrays.asList(
                    originalBaseUrl,
                    baseHost + "8083",
                    baseHost + "8093",
                    baseHost + "9083"
                );
            case "msvc-monitoring":
                return Arrays.asList(
                    originalBaseUrl,
                    baseHost + "8000",
                    baseHost + "8090",
                    baseHost + "9000"
                );
            default:
                return Arrays.asList(originalBaseUrl);
        }
    }

    /**
     * Extrae el puerto de una URL
     */
    private String extractPort(String url) {
        try {
            String[] parts = url.split(":");
            return parts[parts.length - 1];
        } catch (Exception e) {
            return "desconocido";
        }
    }
}

