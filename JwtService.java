package com.example.leavemanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMinutes;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }
    public String createToken(String email, String role) {
        Instant now=Instant.now();
        return Jwts.builder().subject(email).claim("role",role).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expirationMinutes*60))).signWith(key).compact();
    }
    public String username(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject(); }
    public boolean valid(String token) { try { username(token); return true; } catch (JwtException|IllegalArgumentException e) { return false; } }
}
