package com.taller.msvc_security.Controllers;

import com.taller.msvc_security.DTO.LoginRequest;
import com.taller.msvc_security.Services.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final JwtUtils jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        String token = jwtService.generateToken(request.getUsuario());
        Map<String, String> response = Map.of(
                "token", token,
                "type", "Bearer"
        );
        return ResponseEntity.ok(response);
    }


}
