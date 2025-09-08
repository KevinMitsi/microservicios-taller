package com.taller.msvc_security.Controllers;

import com.taller.msvc_security.Entities.Role;
import com.taller.msvc_security.Entities.UserDocument;
import com.taller.msvc_security.Models.*;
import com.taller.msvc_security.Services.UserService;
import com.taller.msvc_security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Tag(name = "usuarios", description = "Operaciones para gestión de usuarios y autenticación")
public class UserController {

    private final UserService userService;

    /**
     * Endpoint para registrar un nuevo usuario
     */
    @PostMapping("/users")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para registrar un usuario",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserRegistrationRequest.class)
            )
    )
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Registra un nuevo usuario en el sistema",
            tags = {"usuarios"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDocument.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<UserDocument> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            UserDocument createdUser = userService.registerUser(request);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Endpoint para listar todos los usuarios de forma paginada
     */
    @GetMapping("/users")
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Listado paginado y filtrado de usuarios. Requiere autenticación Bearer.",
            tags = {"usuarios"},
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDocument.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Número de página", schema = @Schema(type = "integer", defaultValue = "0"))
            @RequestParam(defaultValue = "0") int page,
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Tamaño de página", schema = @Schema(type = "integer", defaultValue = "10"))
            @RequestParam(defaultValue = "10") int size,
            @Parameter(name = "firstName", in = ParameterIn.QUERY, description = "Filtrar por nombre (coincidencia parcial, opcional)", schema = @Schema(type = "string"))
            @RequestParam(required = false) String firstName,
            @Parameter(name = "lastName", in = ParameterIn.QUERY, description = "Filtrar por apellido (coincidencia parcial, opcional)", schema = @Schema(type = "string"))
            @RequestParam(required = false) String lastName) {

        try {
            Pageable paging = PageRequest.of(page, size);
            Page<UserDocument> usersPage = userService.getAllUsersFiltered(paging, firstName, lastName);

            Map<String, Object> response = new HashMap<>();
            response.put("content", usersPage.getContent());
            response.put("totalElements", usersPage.getTotalElements());
            response.put("totalPages", usersPage.getTotalPages());
            response.put("size", usersPage.getSize());
            response.put("number", usersPage.getNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener usuarios", e);
        }
    }

    /**
     * Endpoint para obtener un usuario por su ID
     */
    @GetMapping("/users/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Retorna la información detallada de un usuario por su ID. Requiere autenticación Bearer.",
            tags = {"usuarios"},
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles del usuario",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDocument.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<UserDocument> getUserById(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del usuario (UUID)", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        Optional<UserDocument> userOpt = userService.getUserById(id);

        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id);
        }

        UserDocument user = userOpt.get();
        // Verificar si el usuario actual está intentando acceder a su propio perfil
        if (!user.getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a este recurso");
        }

        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint para actualizar un usuario existente
     */
    @PutMapping("/users/{id}")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para actualizar el usuario",
            required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserUpdateRequest.class))
    )
    @Operation(
            summary = "Actualizar usuario",
            description = "Permite modificar los datos de un usuario existente. Requiere autenticación Bearer.",
            tags = {"usuarios"},
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDocument.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, el usuario ya existe",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<UserDocument> updateUser(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del usuario (UUID)", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id,
            @RequestBody UserUpdateRequest updateRequest) {

        String currentUsername = SecurityUtils.getCurrentUsername();
        Optional<UserDocument> userOpt = userService.getUserById(id);

        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id);
        }

        UserDocument user = userOpt.get();
        if (!user.getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para modificar este usuario");
        }

        try {
            UserDocument updatedUser = userService.updateUser(id, updateRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Endpoint para eliminar un usuario
     */
    @DeleteMapping("/users/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema. Requiere autenticación Bearer.",
            tags = {"usuarios"},
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del usuario (UUID)", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        Optional<UserDocument> userOpt = userService.getUserById(id);

        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id);
        }

        UserDocument user = userOpt.get();
        if (!user.getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este usuario");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para autenticación de usuarios
     */
    @PostMapping("/auth/login")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales de usuario para autenticación",
            required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class))
    )
    @Operation(
            summary = "Inicio de sesión de usuario",
            description = "Autentica un usuario y retorna un token JWT",
            tags = {"autenticación"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = userService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
    }

    /**
     * Endpoint para solicitar recuperación de contraseña
     */
    @PostMapping("/auth/tokens")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Solicitud de recuperación de contraseña (email)",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "object", requiredProperties = {"email"},
                            description = "Ej: {\"email\":\"usuario@ejemplo.com\"}"))
    )
    @Operation(
            summary = "Solicitar recuperación de contraseña",
            description = "Solicitar envío de correo de recuperación",
            tags = {"autenticación"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo de recuperación de contraseña enviado",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"...\"}"))),
            @ApiResponse(responseCode = "400", description = "Email no válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Email no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<Map<String, String>> requestPasswordRecovery(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email es obligatorio");
        }

        try {
            userService.requestPasswordRecovery(email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Se ha enviado un correo con instrucciones para restablecer la contraseña");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email no encontrado");
        }
    }

    /**
     * Endpoint para restablecer contraseña con token y usuario
     */
    @PatchMapping("/users/{id}/password")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Token de recuperación y nueva contraseña. newPassword mínimo 8 caracteres.",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "object", requiredProperties = {"token", "newPassword"},
                            example = "{\"token\":\"abc123\",\"newPassword\":\"NuevaPass123\"}"))
    )
    @Operation(
            summary = "Restablecer contraseña de usuario",
            description = "Permite restablecer la contraseña usando un token de recuperación",
            tags = {"usuarios"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"Contraseña restablecida correctamente\"}"))),
            @ApiResponse(responseCode = "400", description = "Token o contraseña inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<Map<String, String>> resetPassword(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del usuario (UUID)", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || token.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token y nueva contraseña son obligatorios");
        }

        try {
            userService.resetPasswordForUser(id, token, newPassword);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contraseña restablecida correctamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/users/{id}/roles")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Lista de roles a asignar (ej: [\"ADMIN\",\"USER\"])",
            required = true,
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = String.class)))
    )
    @Operation(
            summary = "Actualizar roles de usuario (temporal)",
            description = "Permite a un administrador modificar los roles de un usuario.  \nNota: Este endpoint es temporal y solo debe usarse para pruebas o administración especial.",
            tags = {"usuarios"},
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles actualizados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDocument.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, el usuario ya existe",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<UserDocument> updateUserRoles(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del usuario (UUID)", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id,
            @RequestBody Set<Role> roles) {

        String currentUsername = SecurityUtils.getCurrentUsername();
        Optional<UserDocument> currentUserOpt = userService.getUserByUsername(currentUsername);

        if (currentUserOpt.isEmpty() || !currentUserOpt.get().hasRole(Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permisos para modificar roles");
        }

        try {
            UserDocument updatedUser = userService.updateUserRoles(id, roles);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
