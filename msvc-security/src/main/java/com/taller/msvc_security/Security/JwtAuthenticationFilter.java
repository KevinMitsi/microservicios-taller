package com.taller.msvc_security.Security;


import com.taller.msvc_security.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Omitir filtro JWT para rutas públicas
        if (isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateToken(jwt)) {
                String username = jwtUtils.getUsernameFromToken(jwt);

                // Obtener roles del token o del usuario en la base de datos
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

                // Si usas claims en el JWT para almacenar roles:
                List<String> roles = jwtUtils.getRolesFromToken(jwt);
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Request to: " + request.getRequestURI() + "  Auth header: " +
                                 (parseJwt(request) != null ? "present but invalid" : "missing"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Verifica si la ruta es pública y no necesita autenticación
     */
    private boolean isPublicPath(String path) {
        String[] publicPaths = {
            "/api/auth/login",
            "/api/auth/tokens",
            "/api/auth/password-recovery",
            "/api/auth/password-reset",
            "/health",
            "/actuator/health",
            "/actuator/info",
            "/smoke",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars"
        };

        for (String publicPath : publicPaths) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }

        // También permitir POST a /api/users (registro)
        return path.equals("/api/users");
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}