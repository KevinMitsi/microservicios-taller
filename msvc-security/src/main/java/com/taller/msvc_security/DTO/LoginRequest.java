package com.taller.msvc_security.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginRequest {
    @NotBlank(message = "usuario es obligatorio") String usuario;
    @NotBlank(message = "clave es obligatoria") String clave;
}