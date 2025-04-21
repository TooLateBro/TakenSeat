package com.taken_seat.auth_service.infrastructure.util;

import com.taken_seat.auth_service.domain.entity.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@Data
public class JwtUtil {

    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${service.jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${service.jwt.secret.key}")
    private String secretKey;

    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(User userinfo){
        return Jwts.builder()
                .setId(UUID.randomUUID().toString()) // 토큰의 고유 id를 생성
                .setSubject(String.valueOf(userinfo.getId()))
                .claim("email", userinfo.getEmail())
                .claim("role", userinfo.getRole())
                .setIssuer(issuer)
                .setExpiration(new Date(System.currentTimeMillis()+accessExpiration))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String createRefreshToken(UUID userId){
        return Jwts.builder()
                .claim("userId", userId.toString())
                .setExpiration(new Date(System.currentTimeMillis()+refreshExpiration))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims parseClaimsAllowExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 토큰이 만료됐지만 claims 추출 가능
        }
    }

    public String extractToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7);
    }
}
