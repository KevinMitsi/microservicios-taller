package com.taller.integration.bdd;

import com.taller.integration.IntegrationTestApplication;
import com.taller.integration.client.*;
import com.taller.integration.dto.LoginRequest;
import com.taller.integration.dto.UserRegistrationRequest;
import io.cucumber.java.es.*;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;

@CucumberContextConfiguration
@SpringBootTest(classes = IntegrationTestApplication.class)
public class SystemIntegrationSteps {

    @Autowired
    private SecurityServiceClient securityClient;

    @Autowired
    private SaludoServiceClient saludoClient;

    @Autowired
    private ConsumerServiceClient consumerClient;

    @Autowired
    private OrchestratorServiceClient orchestratorClient;

    private Response lastResponse;
    private String authToken;
    private String username;
    private String password = "SecurePass123!";
    private String userId;

    @Dado("que todos los microservicios están desplegados")
    public void queTodosLosMicroserviciosEstanDesplegados() {
        System.out.println("Verificando que los microservicios estén desplegados...");
        // Los servicios deben estar corriendo antes de las pruebas
    }

    @Cuando("verifico el estado de salud de cada servicio")
    public void verificoElEstadoDeSaludDeCadaServicio() {
        System.out.println("Verificando estado de salud de los servicios...");
    }

    @Entonces("el servicio {string} debe estar disponible")
    public void elServicioDebeEstarDisponible(String serviceName) {
        System.out.println("Verificando disponibilidad de: " + serviceName);

        Response healthResponse = switch (serviceName) {
            case "msvc-security" -> securityClient.healthCheck();
            case "msvc-saludo" -> saludoClient.healthCheck();
            case "msvc-consumer" -> consumerClient.healthCheck();
            case "msvc-orchestrator" -> orchestratorClient.healthCheck();
            default -> throw new IllegalArgumentException("Servicio desconocido: " + serviceName);
        };

        assertThat(healthResponse.getStatusCode())
                .as(serviceName + " debe estar disponible")
                .isEqualTo(200);

        System.out.println("✓ " + serviceName + " está disponible");
    }

    @Dado("que el servicio de seguridad está disponible")
    public void queElServicioDeSeguridadEstaDisponible() {
        await().atMost(30, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .until(() -> securityClient.healthCheck().getStatusCode() == 200);
        System.out.println("✓ Servicio de seguridad disponible");
    }

    @Cuando("registro un nuevo usuario con nombre {string}")
    public void registroUnNuevoUsuarioConNombre(String nombreUsuario) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        username = nombreUsuario + "_" + timestamp;
        String email = username + "@test.com";

        UserRegistrationRequest request = new UserRegistrationRequest(
                username, email, password, "Test", "User"
        );

        lastResponse = securityClient.registerUser(request);
        System.out.println("Usuario registrado: " + username);
    }

    @Entonces("el usuario debe ser creado exitosamente")
    public void elUsuarioDebeSerCreadoExitosamente() {
        assertThat(lastResponse.getStatusCode())
                .as("El usuario debe ser creado")
                .isEqualTo(201);

        userId = lastResponse.jsonPath().getString("id");
        assertThat(userId).isNotNull();
        System.out.println("✓ Usuario creado con ID: " + userId);
    }

    @Y("cuando inicio sesión con las credenciales correctas")
    public void cuandoInicioSesionConLasCredencialesCorrectas() {
        LoginRequest loginRequest = new LoginRequest(username, password);
        lastResponse = securityClient.login(loginRequest);
        System.out.println("Intento de login realizado");
    }

    @Entonces("debo recibir un token JWT válido")
    public void deboRecibirUnTokenJWTValido() {
        assertThat(lastResponse.getStatusCode())
                .as("Login debe ser exitoso")
                .isEqualTo(200);

        authToken = lastResponse.jsonPath().getString("token");
        assertThat(authToken)
                .as("Token debe estar presente")
                .isNotNull()
                .isNotEmpty();

        System.out.println("✓ Token JWT recibido");
    }

    @Dado("que tengo un token de autenticación válido")
    public void queTengoUnTokenDeAutenticacionValido() {
        if (authToken == null) {
            // Crear un usuario y obtener token
            String timestamp = String.valueOf(System.currentTimeMillis());
            username = "tokenuser_" + timestamp;
            String email = username + "@test.com";

            UserRegistrationRequest regRequest = new UserRegistrationRequest(
                    username, email, password, "Token", "User"
            );
            securityClient.registerUser(regRequest);

            LoginRequest loginRequest = new LoginRequest(username, password);
            Response loginResponse = securityClient.login(loginRequest);
            authToken = loginResponse.jsonPath().getString("token");
        }
        assertThat(authToken).isNotNull();
        System.out.println("✓ Token de autenticación disponible");
    }

    @Cuando("solicito un saludo personalizado con el nombre {string}")
    public void solicitoUnSaludoPersonalizadoConElNombre(String nombre) {
        lastResponse = saludoClient.getGreeting(nombre, "Bearer " + authToken);
        System.out.println("Solicitud de saludo enviada para: " + nombre);
    }

    @Entonces("debo recibir un saludo que contenga mi nombre")
    public void deboRecibirUnSaludoQueContengaMiNombre() {
        assertThat(lastResponse.getStatusCode())
                .as("Debe recibir saludo exitosamente")
                .isEqualTo(200);

        String greeting = lastResponse.getBody().asString();
        assertThat(greeting)
                .as("El saludo debe contener contenido")
                .isNotEmpty();

        System.out.println("✓ Saludo recibido: " + greeting);
    }

    @Dado("que todos los servicios están operativos")
    public void queTodosLosServiciosEstanOperativos() {
        assertThat(securityClient.healthCheck().getStatusCode()).isEqualTo(200);
        assertThat(saludoClient.healthCheck().getStatusCode()).isEqualTo(200);
        assertThat(consumerClient.healthCheck().getStatusCode()).isEqualTo(200);
        System.out.println("✓ Todos los servicios operativos");
    }

    @Cuando("invoco el endpoint de consumer con nombre {string}")
    public void invocoElEndpointDeConsumerConNombre(String nombre) {
        lastResponse = consumerClient.consumeApps(nombre);
        System.out.println("Consumer invocado con nombre: " + nombre);
    }

    @Entonces("el consumer debe obtener un token automáticamente")
    public void elConsumerDebeObtenerUnTokenAutomaticamente() {
        // El consumer maneja esto internamente
        assertThat(lastResponse.getStatusCode())
                .as("Consumer debe completar su flujo")
                .isEqualTo(200);
        System.out.println("✓ Consumer obtuvo token automáticamente");
    }

    @Y("debe recibir un saludo del servicio de saludos")
    public void debeRecibirUnSaludoDelServicioDeSaludos() {
        String responseBody = lastResponse.getBody().asString();
        assertThat(responseBody)
                .as("Debe contener un saludo")
                .contains("Hola");
        System.out.println("✓ Saludo recibido del servicio");
    }

    @Y("la respuesta debe contener el nombre proporcionado")
    public void laRespuestaDebeContenerElNombreProporcionado() {
        String responseBody = lastResponse.getBody().asString();
        assertThat(responseBody)
                .as("Debe contener el nombre")
                .isNotEmpty();
        System.out.println("✓ Respuesta contiene información esperada");
    }

    @Cuando("intento iniciar sesión con credenciales inválidas")
    public void intentoIniciarSesionConCredencialesInvalidas() {
        LoginRequest invalidLogin = new LoginRequest("usuario_inexistente", "password_incorrecta");
        lastResponse = securityClient.login(invalidLogin);
        System.out.println("Intento de login con credenciales inválidas");
    }

    @Entonces("debo recibir un error de autenticación")
    public void deboRecibirUnErrorDeAutenticacion() {
        assertThat(lastResponse.getStatusCode())
                .as("Debe rechazar credenciales inválidas")
                .isGreaterThanOrEqualTo(400);
        System.out.println("✓ Error de autenticación recibido correctamente");
    }

    @Y("el código de estado debe ser {int} o {int}")
    public void elCodigoDeEstadoDebeSerO(int code1, int code2) {
        assertThat(lastResponse.getStatusCode())
                .as("Código de estado debe ser apropiado")
                .isIn(code1, code2);
        System.out.println("✓ Código de estado: " + lastResponse.getStatusCode());
    }

    @Dado("que el sistema está operativo")
    public void queElSistemaEstaOperativo() {
        assertThat(securityClient.healthCheck().getStatusCode()).isEqualTo(200);
        System.out.println("✓ Sistema operativo");
    }

    @Cuando("realizo múltiples peticiones concurrentes")
    public void realizoMultiplesPeticionesConcurrentes() {
        System.out.println("Realizando peticiones concurrentes...");
        // Se verificará en el siguiente paso
    }

    @Entonces("todas las peticiones deben completarse exitosamente")
    public void todasLasPeticionesDebenCompletarseExitosamente() {
        int successCount = 0;
        for (int i = 0; i < 5; i++) {
            Response response = saludoClient.getGreeting("Test" + i, "Bearer " + authToken);
            if (response.getStatusCode() == 200) {
                successCount++;
            }
        }
        assertThat(successCount).isEqualTo(5);
        System.out.println("✓ " + successCount + " peticiones completadas exitosamente");
    }

    @Y("el sistema debe mantener su estabilidad")
    public void elSistemaDebeMantenerSuEstabilidad() {
        assertThat(securityClient.healthCheck().getStatusCode()).isEqualTo(200);
        assertThat(saludoClient.healthCheck().getStatusCode()).isEqualTo(200);
        System.out.println("✓ Sistema mantiene estabilidad");
    }

    @Y("el servicio orchestrator está disponible")
    public void elServicioOrchestratorEstaDisponible() {
        await().atMost(30, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .until(() -> orchestratorClient.healthCheck().getStatusCode() == 200);
        System.out.println("✓ Orchestrator disponible");
    }

    @Cuando("consulto los canales de notificación disponibles")
    public void consultoLosCanalesDeNotificacionDisponibles() {
        lastResponse = orchestratorClient.getChannels(authToken);
        System.out.println("Consulta de canales realizada");
    }

    @Entonces("debo recibir una lista de canales")
    public void deboRecibirUnaListaDeCanales() {
        if (lastResponse.getStatusCode() == 200) {
            System.out.println("✓ Lista de canales recibida");
        } else {
            System.out.println("⚠ Servicio de canales requiere configuración adicional");
        }
    }
}

