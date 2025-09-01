package com.taller.msvc_security.Controllers;

import com.taller.msvc_security.Entities.Role;
import com.taller.msvc_security.Entities.UserDocument;
import com.taller.msvc_security.Models.*;
import com.taller.msvc_security.Services.UserService;
import com.taller.msvc_security.utils.SecurityUtils;
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
    @PostMapping("/newUser")
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
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable paging = PageRequest.of(page, size);
            Page<UserDocument> usersPage = userService.getAllUsers(paging);

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
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Endpoint para eliminar un usuario
     */
    @DeleteMapping("/users/{id}")
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = userService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
    }

    /**
     * Endpoint para solicitar recuperación de contraseña
     */
    @PostMapping("/auth/password-recovery")
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
     * Endpoint para restablecer contraseña con token
     */
    @PatchMapping("/auth/password-reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || token.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token y nueva contraseña son obligatorios");
        }

        try {
            userService.resetPassword(token, newPassword);
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
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}