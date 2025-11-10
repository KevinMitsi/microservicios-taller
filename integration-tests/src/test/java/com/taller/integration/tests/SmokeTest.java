package com.taller.integration.tests;

import com.taller.integration.IntegrationTestApplication;
import com.taller.integration.client.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de humo rápida para verificar que todos los servicios están disponibles
 * Esta prueba es más rápida que SystemIntegrationTest y sirve para verificación inicial
 */
@SpringBootTest(classes = IntegrationTestApplication.class)
public class SmokeTest {

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
        try {
            Response securityHealth = securityClient.healthCheck();
            if (securityHealth.getStatusCode() == 200) {
                System.out.println("✓ msvc-security [DISPONIBLE] - Puerto 8080");
            } else {
                System.out.println("✗ msvc-security [NO DISPONIBLE] - Código: " + securityHealth.getStatusCode());
                allServicesUp = false;
            }
        } catch (Exception e) {
            System.out.println("✗ msvc-security [ERROR] - " + e.getMessage());
            allServicesUp = false;
        }

        // Verificar Saludo Service
        try {
            Response saludoHealth = saludoClient.healthCheck();
            if (saludoHealth.getStatusCode() == 200) {
                System.out.println("✓ msvc-saludo [DISPONIBLE] - Puerto 80");
            } else {
                System.out.println("✗ msvc-saludo [NO DISPONIBLE] - Código: " + saludoHealth.getStatusCode());
                allServicesUp = false;
            }
        } catch (Exception e) {
            System.out.println("✗ msvc-saludo [ERROR] - " + e.getMessage());
            allServicesUp = false;
        }

        // Verificar Consumer Service
        try {
            Response consumerHealth = consumerClient.healthCheck();
            if (consumerHealth.getStatusCode() == 200) {
                System.out.println("✓ msvc-consumer [DISPONIBLE] - Puerto 8081");
            } else {
                System.out.println("✗ msvc-consumer [NO DISPONIBLE] - Código: " + consumerHealth.getStatusCode());
                allServicesUp = false;
            }
        } catch (Exception e) {
            System.out.println("✗ msvc-consumer [ERROR] - " + e.getMessage());
            allServicesUp = false;
        }

        // Verificar Orchestrator Service
        try {
            Response orchestratorHealth = orchestratorClient.healthCheck();
            if (orchestratorHealth.getStatusCode() == 200) {
                System.out.println("✓ msvc-orchestrator [DISPONIBLE] - Puerto 8083");
            } else {
                System.out.println("✗ msvc-orchestrator [NO DISPONIBLE] - Código: " + orchestratorHealth.getStatusCode());
                allServicesUp = false;
            }
        } catch (Exception e) {
            System.out.println("✗ msvc-orchestrator [ERROR] - " + e.getMessage());
            allServicesUp = false;
        }

        // Verificar Monitoring Service (opcional)
        try {
            Response monitoringHealth = monitoringClient.healthCheck();
            if (monitoringHealth.getStatusCode() == 200) {
                System.out.println("✓ msvc-monitoring [DISPONIBLE] - Puerto 8000");
            } else {
                System.out.println("⚠ msvc-monitoring [NO DISPONIBLE] - Servicio opcional");
            }
        } catch (Exception e) {
            System.out.println("⚠ msvc-monitoring [NO DISPONIBLE] - Servicio opcional");
        }

        System.out.println("\n" + "=".repeat(60));
        if (allServicesUp) {
            System.out.println("RESULTADO: ✓ TODOS LOS SERVICIOS CRÍTICOS ESTÁN DISPONIBLES");
        } else {
            System.out.println("RESULTADO: ✗ ALGUNOS SERVICIOS NO ESTÁN DISPONIBLES");
            System.out.println("\nPor favor ejecuta: docker-compose up -d");
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
                .isEqualTo(200);

        System.out.println("✓ Conectividad con msvc-security verificada\n");
    }

    @Test
    @DisplayName("Smoke Test: Verificar conectividad básica de saludo")
    public void smokeTest_basicSaludoConnectivity() {
        System.out.println("\n=== SMOKE TEST: Conectividad básica de saludo ===\n");

        Response response = saludoClient.healthCheck();

        assertThat(response.getStatusCode())
                .as("El servicio de saludo debe responder")
                .isEqualTo(200);

        System.out.println("✓ Conectividad con msvc-saludo verificada\n");
    }
}

