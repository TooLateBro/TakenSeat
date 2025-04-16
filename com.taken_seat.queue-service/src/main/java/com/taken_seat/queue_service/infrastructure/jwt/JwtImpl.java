package com.taken_seat.queue_service.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.UUID;

@Component
public class JwtImpl {
    @Value("${spring.application.name}")
    private String issuer;

    private final SecretKey secretKey;

    public JwtImpl(@Value("${service.jwt.secret-key}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public String getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("userId", String.class);
    }

    public String getPerformanceId(String token) {
        Claims claims = getClaims(token);
        return claims.get("performanceId", String.class);
    }

    public String getPerformanceScheduleId(String token) {
        Claims claims = getClaims(token);
        return claims.get("performanceScheduleId", String.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String createAccessToken(UUID userId, UUID performanceId, UUID performanceScheduleId) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("performanceId", performanceId)
                .claim("performanceScheduleId", performanceScheduleId)
                .issuer(issuer)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
}
