package com.taller.msvc_security.Controllers;

import com.taller.msvc_security.Entities.Role;
import com.taller.msvc_security.Entities.UserDocument;
import com.taller.msvc_security.Models.*;
import com.taller.msvc_security.Services.UserService;
import com.taller.msvc_security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class UserController {

    private final UserService userService;

    /**
     * Endpoint para registrar un nuevo usuario
     */
    @PostMapping("/users")
    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en el sistema",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "409", description = "Conflicto, el usuario ya existe"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
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
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios de forma paginada y permite filtrar por nombre y apellido",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuarios listados"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String firstName,
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
            description = "Retorna la información detallada de un usuario por su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<UserDocument> getUserById(@PathVariable String id) {
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
    @Operation(
            summary = "Actualizar usuario",
            description = "Permite modificar los datos de un usuario existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                    @ApiResponse(responseCode = "409", description = "Conflicto, el usuario ya existe"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<UserDocument> updateUser(
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
            description = "Elimina un usuario del sistema",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
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
    @Operation(
            summary = "Inicio de sesión de usuario",
            description = "Autentica un usuario y retorna un token JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
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
    @Operation(
            summary = "Solicitar recuperación de contraseña",
            description = "Solicita el envío de un correo con instrucciones para recuperar contraseña",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Correo de recuperación de contraseña enviado"),
                    @ApiResponse(responseCode = "400", description = "Email no válido"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
                    @ApiResponse(responseCode = "404", description = "Email no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
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
    @Operation(
            summary = "Restablecer contraseña de usuario",
            description = "Permite restablecer la contraseña usando un token de recuperación",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Token o contraseña inválida"),
                    @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<Map<String, String>> resetPassword(
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
    @Operation(
            summary = "Actualizar roles de usuario (temporal)",
            description = "Permite a un administrador modificar los roles de un usuario.  \n**Nota:** Este endpoint es temporal y solo debe usarse para pruebas o administración especial.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Roles actualizados"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                    @ApiResponse(responseCode = "409", description = "Conflicto, el usuario ya existe"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<UserDocument> updateUserRoles(
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