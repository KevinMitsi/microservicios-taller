package com.taller.msvc_security.Services.Implementation;

import com.taller.msvc_security.Entities.PasswordResetToken;
import com.taller.msvc_security.Entities.Role;
import com.taller.msvc_security.Entities.UserDocument;
import com.taller.msvc_security.Models.*;
import com.taller.msvc_security.Repository.PasswordResetTokenRepository;
import com.taller.msvc_security.Repository.UserRepository;
import com.taller.msvc_security.exception.InvalidCredentialsException;
import com.taller.msvc_security.exception.UserAlreadyExistException;
import com.taller.msvc_security.Services.UserEventService;
import com.taller.msvc_security.utils.JwtUtils;
import com.taller.msvc_security.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserEventService userEventService;
    private final JwtUtils jwtUtils;

    @Value("${jwt.expiration-minutes:60}")
    private Integer jwtExpirationMinutes;


    private void publishUserEvent(String eventType, UserDocument user, Map<String, Object> additionalData) {
        UserEvent event = new UserEvent();
        event.setEventType(eventType);
        event.setUserId(user.getId());
        event.setUsername(user.getUsername());
        event.setEmail(user.getEmail());
        event.setMobileNumber(user.getMobileNumber());
        event.setTimestamp(LocalDateTime.now());
        event.setAdditionalData(additionalData != null ? additionalData : new HashMap<>());

        userEventService.publishEvent(event);
    }

    // -------------------------
    // REGLAS DE NEGOCIO
    // -------------------------

    @Override
    @Transactional
    public UserDocument registerUser(UserRegistrationRequest registrationRequest) {
        if (existsByUsername(registrationRequest.getUsername())) {
            throw new UserAlreadyExistException("El nombre de usuario ya está en uso");
        }
        if (existsByEmail(registrationRequest.getEmail())) {
            throw new UserAlreadyExistException("El correo electrónico ya está registrado");
        }

        UserDocument user = mapUserDocument(registrationRequest);
        UserDocument saved = userRepository.save(user);

        // Publicar evento de nuevo usuario
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", saved.getFirstName());
        data.put("lastName", saved.getLastName());

        publishUserEvent("new-user", saved, data);

        return saved;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDocument user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String jwt = jwtUtils.generateToken(user.getUsername(), user.getAuthorities());

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(jwt);
            authResponse.setTokenType("Bearer");
            authResponse.setExpiresIn(jwtExpirationMinutes * 60);
            authResponse.setUser(user);

            // Publicar evento de login exitoso
            Map<String, Object> data = new HashMap<>();
            data.put("loginTime", LocalDateTime.now());

            publishUserEvent("login", user, data);

            return authResponse;
        } catch (Exception e) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
    }

    @Override
    @Transactional
    public void requestPasswordRecovery(String email) {
        UserDocument user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No se encontró un usuario con este correo electrónico"));

        LocalDateTime now = LocalDateTime.now();
        if (tokenRepository.existsByUserIdAndExpiryDateAfter(user.getId(), now)) {
            tokenRepository.deleteByUserId(user.getId());
        }

        String tokenStr = UUID.randomUUID().toString();
        LocalDateTime expiryDate = now.plusHours(1);
        PasswordResetToken resetToken = new PasswordResetToken(null, tokenStr, user.getId(), expiryDate);
        tokenRepository.save(resetToken);

        // Publicar evento de recuperación de contraseña
        Map<String, Object> data = new HashMap<>();
        data.put("token", tokenStr);
        data.put("expiry", expiryDate);

        publishUserEvent("password-recovery", user, data);
    }

    @Override
    @Transactional
    public void resetPasswordForUser(String userId, String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new InvalidOneTimeTokenException("El token ha expirado");
        }
        if (!resetToken.getUserId().equals(userId)) {
            throw new InvalidOneTimeTokenException("El token no corresponde al usuario");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres");
        }

        UserDocument user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        // Publicar evento de actualización de contraseña
        Map<String, Object> data = new HashMap<>();
        data.put("updateTime", LocalDateTime.now());

        publishUserEvent("password-update", user, data);
    }

    // -------------------------
    // CRUD y utilitarios
    // -------------------------

    @Override
    @Transactional
    public UserDocument updateUser(String id, UserUpdateRequest updateRequest) {
        UserDocument user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (updateRequest.getEmail() != null &&
                !updateRequest.getEmail().equals(user.getEmail()) &&
                existsByEmail(updateRequest.getEmail())) {
            throw new UserAlreadyExistException("El correo electrónico ya está en uso por otro usuario");
        }

        if (updateRequest.getFirstName() != null) user.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null) user.setLastName(updateRequest.getLastName());
        if (updateRequest.getEmail() != null) user.setEmail(updateRequest.getEmail());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        if (userRepository.existsById(id)) {
            tokenRepository.deleteByUserId(id);
            userRepository.deleteById(id);
        }
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
    public Optional<UserDocument> getUserByUsername(String username) {
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
    public Page<UserDocument> getAllUsersFiltered(Pageable pageable, String firstName, String lastName) {
        if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
            return userRepository.findAll(pageable);
        }
        if (firstName != null && !firstName.isBlank() && (lastName == null || lastName.isBlank())) {
            return userRepository.findByFirstNameContainingIgnoreCase(firstName, pageable);
        }
        if ((firstName == null || firstName.isBlank()) && lastName != null && !lastName.isBlank()) {
            return userRepository.findByLastNameContainingIgnoreCase(lastName, pageable);
        }
        return userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName, pageable);
    }

    private UserDocument mapUserDocument(UserRegistrationRequest registrationRequest) {
        UserDocument user = new UserDocument();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setMobileNumber(registrationRequest.getMobileNumber());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.addRole(Role.USER);
        return user;
    }

    @Override
    public Page<UserDocument> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<UserDocument> getUserById(String id) {
        return userRepository.findById(id);
    }
}
