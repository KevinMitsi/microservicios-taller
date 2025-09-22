package com.taller.msvc_orchestrator.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {

    private String templateType;

    @NotBlank(message = "El canal es obligatorio")
    private String channel;

    @NotBlank(message = "El destino es obligatorio")
    private String destination;

    @NotBlank(message = "El asunto es obligatorio")
    private String subject;

    @NotBlank(message = "El cuerpo es obligatorio")
    private String body;

    private Map<String,Object> data;

    @Future(message = "La fecha de envío debe ser en el futuro")
    private Instant sendAt;
}