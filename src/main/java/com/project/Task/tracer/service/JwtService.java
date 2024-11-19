package com.project.Task.tracer.service;

import com.project.Task.tracer.model.user.Role;
import com.project.Task.tracer.security.AppUserPrincipal;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expirationTimeAccessToken}")
    private String expirationTimeAccessToken;

    @Value("${app.jwt.expirationTimeRefreshToken}")
    private String expirationTimeRefreshToken;


    public String generateAccessToken(AppUserPrincipal userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUser().getId().toString())
                .claim("roles", userDetails.getUser().getRoles().stream()
                        .map(Role::getRole).collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(expirationTimeAccessToken)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateAccessToken(UUID userId, List<Role> roles) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("roles", roles.stream()
                        .map(Role::getRole).collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(expirationTimeAccessToken)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(AppUserPrincipal userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUser().getId().toString())
                .claim("roles", userDetails.getUser().getRoles().stream()
                        .map(Role::getRole).collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(expirationTimeRefreshToken)))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            Date expiration = Jwts.parser().verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload().getExpiration();
            return !expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUUID(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public List<String> getRoles(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
