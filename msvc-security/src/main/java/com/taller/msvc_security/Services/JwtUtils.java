package com.taller.msvc_security.Services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretBase64;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration-minutes:60}")
    private long expirationMinutes;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String usuario) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES));

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims()
                .subject(usuario)
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .add("typ", "Bearer")
                .and()
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)  // parseClaimsJws() → parseSignedClaims()
                    .getPayload();             // getBody() → getPayload()

            String tokenIssuer = claims.getIssuer();
            return issuer.equals(tokenIssuer) && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)  // parseClaimsJws() → parseSignedClaims()
                .getPayload()              // getBody() → getPayload()
                .getSubject();
    }

    // Método adicional para obtener claims específicos si los necesitas
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)  // parseClaimsJws() → parseSignedClaims()
                .getPayload();             // getBody() → getPayload()
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimsFromToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;

        }
    }
}