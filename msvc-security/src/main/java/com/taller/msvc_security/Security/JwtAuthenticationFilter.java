package com.taller.msvc_security.Security;


import com.taller.msvc_security.Services.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println("Request to: " + request.getRequestURI() + " | Auth header: " +
                (authHeader != null ? "present" : "missing"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No hay token -> continuar (Security exigirá autenticación si endpoint protegido)
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        if (!jwtUtils.validateToken(token)) {
            // Token inválido -> responder 401 y no continuar
            respondUnauthorized(response);
            return;
        }

        String username = jwtUtils.getUsernameFromToken(token);

        // Aquí puedes mapear roles/claims si los incluyes en el token.
        // Por simplicidad asignamos ROLE_USER
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void respondUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> body = Map.of("error", "Token inválido o expirado");
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
