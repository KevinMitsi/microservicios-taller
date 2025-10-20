package com.taller.msvc_security.utils;

import com.github.javafaker.Faker;

import com.taller.msvc_security.Models.LoginRequest;
import com.taller.msvc_security.Models.UserRegistrationRequest;
import com.taller.msvc_security.Models.UserUpdateRequest;

import java.util.Locale;


/**
 * Utilidad para generar datos aleatorios en los tests usando JavaFaker
 */
public class TestDataGenerator {

    private static final Faker faker = new Faker(Locale.forLanguageTag("es"));

    /**
     * Genera un UserRegistrationRequest con datos aleatorios
     */
    public static UserRegistrationRequest generateRandomUserRegistration() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(faker.name().username());
        request.setEmail(faker.internet().emailAddress());
        request.setPassword(faker.internet().password(8, 16, true, true));
        request.setFirstName(faker.name().firstName());
        request.setLastName(faker.name().lastName());
        request.setMobileNumber(faker.phoneNumber().cellPhone());
        return request;
    }

    /**
     * Genera un UserRegistrationRequest con email específico
     */
    public static UserRegistrationRequest generateUserRegistrationWithEmail(String email) {
        UserRegistrationRequest request = generateRandomUserRegistration();
        request.setEmail(email);
        return request;
    }

    /**
     * Genera un UserRegistrationRequest con username específico
     */
    public static UserRegistrationRequest generateUserRegistrationWithUsername(String username) {
        UserRegistrationRequest request = generateRandomUserRegistration();
        request.setUsername(username);
        return request;
    }

    /**
     * Genera un UserUpdateRequest con datos aleatorios
     */
    public static UserUpdateRequest generateRandomUserUpdate() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName(faker.name().firstName());
        request.setLastName(faker.name().lastName());
        request.setMobileNumber(faker.phoneNumber().cellPhone());
        return request;
    }

    /**
     * Genera un LoginRequest con credenciales aleatorias
     */
    public static LoginRequest generateRandomLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername(faker.name().username());
        request.setPassword(faker.internet().password(8, 16));
        return request;
    }

    /**
     * Genera un LoginRequest con username específico
     */
    public static LoginRequest generateLoginRequestWithUsername(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    /**
     * Genera un nombre aleatorio
     */
    public static String generateFirstName() {
        return faker.name().firstName();
    }

    /**
     * Genera un apellido aleatorio
     */
    public static String generateLastName() {
        return faker.name().lastName();
    }

    /**
     * Genera un email aleatorio
     */
    public static String generateEmail() {
        return faker.internet().emailAddress();
    }

    /**
     * Genera un username aleatorio
     */
    public static String generateUsername() {
        return faker.name().username();
    }

    /**
     * Genera una contraseña aleatoria
     */
    public static String generatePassword() {
        return faker.internet().password(8, 16, true, true);
    }

    /**
     * Genera un número de teléfono aleatorio
     */
    public static String generatePhoneNumber() {
        return faker.phoneNumber().cellPhone();
    }

    /**
     * Genera un token aleatorio
     */
    public static String generateToken() {
        return faker.regexify("[a-zA-Z0-9]{32}");
    }

    /**
     * Obtiene la instancia de Faker para usos personalizados
     */
    public static Faker getFaker() {
        return faker;
    }
}
