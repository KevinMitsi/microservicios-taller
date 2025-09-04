package com.taller.msvc_security.Services.Implementation;

import com.taller.msvc_security.Entities.PasswordResetToken;
import com.taller.msvc_security.Entities.Role;
import com.taller.msvc_security.Entities.UserDocument;
import com.taller.msvc_security.Models.AuthResponse;
import com.taller.msvc_security.Models.LoginRequest;
import com.taller.msvc_security.Models.UserRegistrationRequest;
import com.taller.msvc_security.Models.UserUpdateRequest;
import com.taller.msvc_security.Repository.PasswordResetTokenRepository;
import com.taller.msvc_security.Repository.UserRepository;
import com.taller.msvc_security.exception.InvalidCredentialsException;
import com.taller.msvc_security.exception.UserAlreadyExistException;
import com.taller.msvc_security.utils.JwtUtils;
import com.taller.msvc_security.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Value("${jwt.expiration-minutes:60}")
    private Integer jwtExpirationMinutes;

    @Override
    @Transactional
    public UserDocument registerUser(UserRegistrationRequest registrationRequest) {
        // Validar que el usuario no exista
        if (existsByUsername(registrationRequest.getUsername())) {
            throw new UserAlreadyExistException("El nombre de usuario ya está en uso");
        }

        if (existsByEmail(registrationRequest.getEmail())) {
            throw new UserAlreadyExistException("El correo electrónico ya está registrado");
        }

        // Validar datos de entrada
        if (registrationRequest.getUsername() == null || registrationRequest.getUsername().length() < 3) {
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 3 caracteres");
        }

        if (registrationRequest.getPassword() == null || registrationRequest.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        // Crear nuevo usuario usando Lombok
        UserDocument user = new UserDocument();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());

        user.addRole(Role.USER);
        return userRepository.save(user);
    }

    @Override
    public Page<UserDocument> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<UserDocument> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserDocument updateUser(String id, UserUpdateRequest updateRequest) {
        // Buscar el usuario por ID
        UserDocument user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Verificar si el email ya está en uso por otro usuario
        if (updateRequest.getEmail() != null &&
                !updateRequest.getEmail().equals(user.getEmail()) &&
                existsByEmail(updateRequest.getEmail())) {
            throw new UserAlreadyExistException("El correo electrónico ya está en uso por otro usuario");
        }

        // Actualizar los campos que no son nulos - podemos usar los setters gracias a @Data
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }

        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }

        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        if (userRepository.existsById(id)) {
            // Eliminar tokens asociados primero
            tokenRepository.deleteByUserId(id);
            // Eliminar el usuario
            userRepository.deleteById(id);
        }
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Autenticar las credenciales
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener usuario autenticado
            UserDocument user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Generar token JWT con roles
            String jwt = jwtUtils.generateToken(user.getUsername(), user.getAuthorities());

            // Crear respuesta de autenticación
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(jwt);
            authResponse.setTokenType("Bearer");
            authResponse.setExpiresIn(jwtExpirationMinutes * 60); // minutos → segundos
            authResponse.setUser(user);

            return authResponse;
        } catch (Exception e) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
    }


    @Override
        @Transactional
        public void requestPasswordRecovery(String email) {
            // Buscar usuario por email
            UserDocument user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("No se encontró un usuario con este correo electrónico"));

            // Verificar si ya existe un token válido
            LocalDateTime now = LocalDateTime.now();
            if (tokenRepository.existsByUserIdAndExpiryDateAfter(user.getId(), now)) {
                // Si existe, eliminar tokens anteriores
                tokenRepository.deleteByUserId(user.getId());
            }

            // Generar nuevo token - podemos usar el constructor con argumentos gracias a @AllArgsConstructor
            String tokenStr = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // expira en 1 hora
            PasswordResetToken resetToken = new PasswordResetToken(
                    null,                // id (MongoDB lo genera solo)
                    tokenStr,            // token generado con UUID
                    user.getId(),        // id del usuario
                    expiryDate           // fecha de expiración
            );

            tokenRepository.save(resetToken);



    }

    @Override
    public Optional<UserDocument> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<UserDocument> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<UserDocument> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining(
                searchTerm, searchTerm, searchTerm, searchTerm, pageable);
    }

    @Override
    // En UserService.java
    public Optional<UserDocument> getUserByUsername(String username) {
        // Implementación para buscar un usuario por su nombre de usuario
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDocument updateUserRoles(String id, Set<Role> roles) {
        UserDocument user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        user.setAuthorities(roles);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPasswordForUser(String userId, String token, String newPassword) {
        // Validar el token
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // Verificar si el token ha expirado
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new InvalidOneTimeTokenException("El token ha expirado");
        }

        // Verificar que el token corresponde al usuario
        if (!resetToken.getUserId().equals(userId)) {
            throw new RuntimeException("El token no corresponde al usuario");
        }

        // Validar la nueva contraseña
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres");
        }

        // Buscar el usuario asociado al token
        UserDocument user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar la contraseña del usuario
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Eliminar el token usado
        tokenRepository.delete(resetToken);
    }

}

