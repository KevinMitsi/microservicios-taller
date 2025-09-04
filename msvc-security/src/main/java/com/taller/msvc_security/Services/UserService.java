package com.taller.msvc_security.Services;

import com.taller.msvc_security.Entities.Role;
import com.taller.msvc_security.Entities.UserDocument;
import com.taller.msvc_security.Models.AuthResponse;
import com.taller.msvc_security.Models.UserRegistrationRequest;
import com.taller.msvc_security.Models.UserUpdateRequest;
import com.taller.msvc_security.Models.LoginRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    /**
     * Registra un nuevo usuario en el sistema
     * @param registrationRequest datos del usuario a registrar
     * @return el usuario creado
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws RuntimeException si ya existe un usuario con el mismo username o email
     */
    UserDocument registerUser(UserRegistrationRequest registrationRequest);

    /**
     * Obtiene una lista paginada de usuarios
     * @param pageable configuración de paginación
     * @return página de usuarios
     */
    Page<UserDocument> getAllUsers(Pageable pageable);

    /**
     * Busca un usuario por su ID
     * @param id identificador del usuario
     * @return usuario encontrado o empty si no existe
     */
    Optional<UserDocument> getUserById(String id);

    /**
     * Actualiza los datos de un usuario existente
     * @param id identificador del usuario a actualizar
     * @param updateRequest datos a actualizar
     * @return usuario actualizado
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws RuntimeException si no se encuentra el usuario
     */
    UserDocument updateUser(String id, UserUpdateRequest updateRequest);

    /**
     * Elimina un usuario del sistema
     *
     * @param id identificador del usuario a eliminar
     */
    void deleteUser(String id);

    /**
     * Realiza la autenticación de un usuario
     * @param loginRequest credenciales de inicio de sesión
     * @return respuesta con token de autenticación y datos del usuario
     * @throws RuntimeException si las credenciales son inválidas
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * Inicia el proceso de recuperación de contraseña
     *
     * @param email correo del usuario
     */
    void requestPasswordRecovery(String email);

    /**
     * Restablece la contraseña de un usuario usando un token de recuperación
     *
     * @param userId      id del usuario
     * @param token       token de recuperación
     * @param newPassword nueva contraseña
     * @throws RuntimeException si el token es inválido o ha expirado
     */
    void resetPasswordForUser(String userId, String token, String newPassword);

    /**
     * Busca un usuario por su nombre de usuario
     * @param username nombre de usuario
     * @return usuario encontrado o empty si no existe
     */
    Optional<UserDocument> findByUsername(String username);

    /**
     * Busca un usuario por su correo electrónico
     * @param email correo electrónico
     * @return usuario encontrado o empty si no existe
     */
    Optional<UserDocument> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     * @param username nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el correo electrónico dado
     * @param email correo electrónico a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios por criterios específicos
     * @param searchTerm término de búsqueda para username, email, firstName o lastName
     * @param pageable configuración de paginación
     * @return página de usuarios que coinciden con la búsqueda
     */
    Page<UserDocument> searchUsers(String searchTerm, Pageable pageable);
    Optional<UserDocument>getUserByUsername(String username);
    UserDocument updateUserRoles(String id, Set<Role> roles);
}