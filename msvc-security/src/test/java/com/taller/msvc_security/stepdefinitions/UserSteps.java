package com.taller.msvc_security.stepdefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.msvc_security.Entities.Role;
import com.taller.msvc_security.Entities.UserDocument;
import com.taller.msvc_security.Models.AuthResponse;
import com.taller.msvc_security.Models.LoginRequest;
import com.taller.msvc_security.Models.UserRegistrationRequest;
import com.taller.msvc_security.Models.UserUpdateRequest;
import com.taller.msvc_security.Repository.UserRepository;
import com.taller.msvc_security.Services.UserService;
import com.taller.msvc_security.utils.JwtUtils;
import com.taller.msvc_security.utils.TestDataGenerator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private com.taller.msvc_security.Repository.PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private ResultActions resultActions;
    private MvcResult mvcResult;
    private UserDocument testUser;
    private String jwtToken;

    @Before
    public void setup() {
        // Limpiar mocks antes de cada escenario
        if (userRepository != null && userService != null) {
            reset(userRepository, userService);
        }
    }

    // ==================== GIVEN STEPS ====================

    @Given("el servicio de usuarios está disponible")
    public void elServicioDeUsuariosEstaDisponible() {
        // El servicio está disponible por defecto en el contexto de Spring
        assertThat(mockMvc).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Given("existe un usuario con username {string} y password {string}")
    public void existeUnUsuarioConUsernameYPassword(String username, String password) {
        testUser = new UserDocument();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setUsername(username);
        testUser.setEmail("juan@example.com");
        testUser.setPassword(passwordEncoder.encode(password));
        testUser.setFirstName("Juan");
        testUser.setLastName("Pérez");
        testUser.setMobileNumber("3140000000");
        testUser.setAuthorities(new HashSet<>(Collections.singletonList(Role.USER)));

        // Mock del repositorio
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Mock del servicio de login
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("mock-jwt-token-12345");
        authResponse.setExpiresIn(3600);
        authResponse.setTokenType("Bearer");
        authResponse.setUser(testUser);

        when(userService.login(any(LoginRequest.class))).thenReturn(authResponse);
    }

    @Given("existe un usuario con email {string}")
    public void existeUnUsuarioConEmail(String email) {
        testUser = new UserDocument();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setUsername("juanperez");
        testUser.setEmail(email);
        testUser.setPassword(passwordEncoder.encode("supersegura1"));
        testUser.setFirstName("Juan");
        testUser.setLastName("Pérez");
        testUser.setMobileNumber("3140000000");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        doNothing().when(userService).requestPasswordRecovery(email);
    }

    @Given("existe un usuario con id {string} y username {string}")
    public void existeUnUsuarioConIdYUsername(String id, String username) {
        testUser = new UserDocument();
        testUser.setId(id);
        testUser.setUsername(username);
        testUser.setEmail("juan@example.com");
        testUser.setPassword(passwordEncoder.encode("supersegura1"));
        testUser.setFirstName("Juan");
        testUser.setLastName("Pérez");
        testUser.setMobileNumber("3140000000");
        testUser.setAuthorities(new HashSet<>(Collections.singletonList(Role.USER)));

        // Generar un token JWT válido para autenticación
        jwtToken = jwtUtils.generateToken(username, testUser.getAuthorities());

        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userService.getUserById(id)).thenReturn(Optional.of(testUser));

        // Mock para el update
        UserDocument updatedUser = new UserDocument();
        updatedUser.setId(id);
        updatedUser.setUsername(username);
        updatedUser.setEmail("juan@example.com");
        updatedUser.setPassword(testUser.getPassword());
        updatedUser.setFirstName("Juanito"); // Nombre actualizado
        updatedUser.setLastName("Pérez");
        updatedUser.setMobileNumber("3140000000");

        when(userService.updateUser(eq(id), any(UserUpdateRequest.class))).thenReturn(updatedUser);
    }

    // ==================== WHEN STEPS ====================

    @When("envío una solicitud POST a \\/users\\/register con los datos:")
    public void envioUnaSolicitudPOSTAUsersRegisterConLosDatos(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(removeQuotes(data.get("username")));
        request.setEmail(removeQuotes(data.get("email")));
        request.setPassword(removeQuotes(data.get("password")));
        request.setFirstName(removeQuotes(data.get("firstName")));
        request.setLastName(removeQuotes(data.get("lastName")));
        request.setMobileNumber(removeQuotes(data.get("mobileNumber")));

        // Crear el usuario que será retornado
        UserDocument createdUser = new UserDocument();
        createdUser.setId(UUID.randomUUID().toString());
        createdUser.setUsername(request.getUsername());
        createdUser.setEmail(request.getEmail());
        createdUser.setPassword(passwordEncoder.encode(request.getPassword()));
        createdUser.setFirstName(request.getFirstName());
        createdUser.setLastName(request.getLastName());
        createdUser.setMobileNumber(request.getMobileNumber());
        createdUser.setAuthorities(new HashSet<>(Collections.singletonList(Role.USER)));

        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(createdUser);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        testUser = createdUser;

        resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mvcResult = resultActions.andReturn();
    }

    @When("envío una solicitud POST a \\/users\\/login con:")
    public void envioUnaSolicitudPOSTAUsersLoginCon(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(removeQuotes(data.get("username")));
        loginRequest.setPassword(removeQuotes(data.get("password")));

        resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        mvcResult = resultActions.andReturn();
    }

    @When("envío una solicitud POST a \\/users\\/password-recovery con:")
    public void envioUnaSolicitudPOSTAUsersPasswordRecoveryCon(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String email = removeQuotes(data.get("email"));

        Map<String, String> request = new HashMap<>();
        request.put("email", email);

        resultActions = mockMvc.perform(post("/api/auth/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mvcResult = resultActions.andReturn();
    }

    @When("envío una solicitud PUT a \\/users\\/{int} con:")
    public void envioUnaSolicitudPUTAUsersCon(Integer id, DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        if (data.containsKey("firstName")) {
            updateRequest.setFirstName(removeQuotes(data.get("firstName")));
        }
        if (data.containsKey("lastName")) {
            updateRequest.setLastName(removeQuotes(data.get("lastName")));
        }
        if (data.containsKey("email")) {
            updateRequest.setEmail(removeQuotes(data.get("email")));
        }
        if (data.containsKey("mobileNumber")) {
            updateRequest.setMobileNumber(removeQuotes(data.get("mobileNumber")));
        }

        resultActions = mockMvc.perform(put("/api/users/" + id)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        mvcResult = resultActions.andReturn();
    }

    // ==================== THEN STEPS ====================

    @Then("recibo una respuesta {int} y el usuario es creado con el username {string}")
    public void reciboUnaRespuestaYElUsuarioEsCreadoConElUsername(Integer statusCode, String expectedUsername) throws Exception {
        resultActions.andExpect(status().is(statusCode));

        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDocument responseUser = objectMapper.readValue(responseBody, UserDocument.class);

        assertThat(responseUser).isNotNull();
        assertThat(responseUser.getUsername()).isEqualTo(removeQuotes(expectedUsername));
        assertThat(responseUser.getId()).isNotNull();

        verify(userService, times(1)).registerUser(any(UserRegistrationRequest.class));
    }

    @Then("recibo un token JWT válido en la respuesta y código {int}")
    public void reciboUnTokenJWTValidoEnLaRespuestaYCodigo(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));

        String responseBody = mvcResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);

        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getToken()).isNotNull();
        assertThat(authResponse.getToken()).isNotEmpty();
        assertThat(authResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(authResponse.getExpiresIn()).isGreaterThan(0);

        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Then("recibo una respuesta {int} y se envía el evento de recuperación de contraseña")
    public void reciboUnaRespuestaYSeEnviaElEventoDeRecuperacionDeContrasena(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody).contains("message");
        assertThat(responseBody).contains("contraseña");

        verify(userService, times(1)).requestPasswordRecovery(anyString());
    }

    @Then("recibo una respuesta {int} y el nombre del usuario se actualiza a {string}")
    public void reciboUnaRespuestaYElNombreDelUsuarioSeActualizaA(Integer statusCode, String expectedFirstName) throws Exception {
        resultActions.andExpect(status().is(statusCode));

        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDocument responseUser = objectMapper.readValue(responseBody, UserDocument.class);

        assertThat(responseUser).isNotNull();
        assertThat(responseUser.getFirstName()).isEqualTo(removeQuotes(expectedFirstName));

        verify(userService, times(1)).updateUser(anyString(), any(UserUpdateRequest.class));
    }

    // ==================== HELPER METHODS ====================

    private String removeQuotes(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("^\"|\"$", "");
    }

    // ==================== STEP DEFINITIONS CON JAVAFAKER ====================

    private final List<UserDocument> createdUsers = new ArrayList<>();
    private UserDocument randomUser;
    private String randomUserPassword;

    @When("creo {int} usuarios con datos aleatorios")
    public void creoUsuariosConDatosAleatorios(Integer count) throws Exception {
        createdUsers.clear();

        for (int i = 0; i < count; i++) {
            // Generar datos aleatorios usando JavaFaker
            UserRegistrationRequest request = TestDataGenerator.generateRandomUserRegistration();

            // Crear el usuario que será retornado por el mock
            UserDocument createdUser = new UserDocument();
            createdUser.setId(UUID.randomUUID().toString());
            createdUser.setUsername(request.getUsername());
            createdUser.setEmail(request.getEmail());
            createdUser.setPassword(passwordEncoder.encode(request.getPassword()));
            createdUser.setFirstName(request.getFirstName());
            createdUser.setLastName(request.getLastName());
            createdUser.setMobileNumber(request.getMobileNumber());
            createdUser.setAuthorities(new HashSet<>(Collections.singletonList(Role.USER)));

            // Configurar mocks antes de la petición
            when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(createdUser);
            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

            // Realizar la petición
            resultActions = mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            resultActions.andExpect(status().isCreated());

            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            UserDocument responseUser = objectMapper.readValue(responseBody, UserDocument.class);

            createdUsers.add(responseUser);
        }
    }

    @Then("todos los usuarios son creados exitosamente")
    public void todosLosUsuariosSonCreadosExitosamente() {
        assertThat(createdUsers).isNotEmpty();
        assertThat(createdUsers).hasSize(5);

        for (UserDocument user : createdUsers) {
            assertThat(user.getId()).isNotNull();
            assertThat(user.getUsername()).isNotNull();
            assertThat(user.getEmail()).isNotNull();
        }
    }

    @Then("cada usuario tiene datos únicos")
    public void cadaUsuarioTieneDatosUnicos() {
        Set<String> usernames = new HashSet<>();
        Set<String> emails = new HashSet<>();

        for (UserDocument user : createdUsers) {
            // Verificar que username es único
            assertThat(usernames.add(user.getUsername()))
                    .as("Username duplicado encontrado: " + user.getUsername())
                    .isTrue();

            // Verificar que email es único
            assertThat(emails.add(user.getEmail()))
                    .as("Email duplicado encontrado: " + user.getEmail())
                    .isTrue();

            // Verificar que los campos tienen contenido
            assertThat(user.getFirstName()).isNotBlank();
            assertThat(user.getLastName()).isNotBlank();
            assertThat(user.getMobileNumber()).isNotBlank();
        }
    }

    @Given("existe un usuario aleatorio en el sistema")
    public void existeUnUsuarioAleatorioEnElSistema() {
        // Generar usuario aleatorio con JavaFaker
        UserRegistrationRequest request = TestDataGenerator.generateRandomUserRegistration();
        randomUserPassword = request.getPassword(); // Guardar la contraseña para usarla después

        randomUser = new UserDocument();
        randomUser.setId(UUID.randomUUID().toString());
        randomUser.setUsername(request.getUsername());
        randomUser.setEmail(request.getEmail());
        randomUser.setPassword(passwordEncoder.encode(request.getPassword()));
        randomUser.setFirstName(request.getFirstName());
        randomUser.setLastName(request.getLastName());
        randomUser.setMobileNumber(request.getMobileNumber());
        randomUser.setAuthorities(new HashSet<>(Collections.singletonList(Role.USER)));

        // Mock del repositorio
        when(userRepository.findByUsername(randomUser.getUsername())).thenReturn(Optional.of(randomUser));
        when(userRepository.findById(randomUser.getId())).thenReturn(Optional.of(randomUser));
    }

    @When("intento hacer login con las credenciales correctas")
    public void intentoHacerLoginConLasCredencialesCorrectas() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(randomUser.getUsername());
        loginRequest.setPassword(randomUserPassword);

        // Mock del servicio de login
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(TestDataGenerator.generateToken());
        authResponse.setExpiresIn(3600);
        authResponse.setTokenType("Bearer");
        authResponse.setUser(randomUser);

        when(userService.login(any(LoginRequest.class))).thenReturn(authResponse);

        resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
    }

    @Then("el login es exitoso con código {int}")
    public void elLoginEsExitosoConCodigo(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Then("recibo un token JWT válido")
    public void reciboUnTokenJWTValido() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);

        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getToken()).isNotNull();
        assertThat(authResponse.getToken()).isNotEmpty();
        assertThat(authResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(authResponse.getExpiresIn()).isGreaterThan(0);
        assertThat(authResponse.getUser()).isNotNull();
        assertThat(authResponse.getUser().getUsername()).isEqualTo(randomUser.getUsername());
    }
}
