package com.taken_seat.gateway_service.filter;

import com.taken_seat.gateway_service.exception.customException.GatewayException;
import com.taken_seat.gateway_service.exception.enums.ResponseCode;
import com.taken_seat.gateway_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BLACKLIST_PREFIX = "BLACKLIST:";

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/v1/auths/signUp",
            "/api/v1/auths/login",
            "/v3/api-docs",
            "/swagger-ui"
    );
    private static final String LOGOUT_PATH = "/api/v1/auths/logout";

    private final JwtUtil jwtUtil;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.info("{} {} 요청이 들어왔습니다.", method, path);

        if (isExcludedPath(path) && !LOGOUT_PATH.equals(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = jwtUtil.extractToken(authHeader);

        if (LOGOUT_PATH.equals(path)) {
            return handleLogout(token, chain, exchange);
        }

        if (token == null) {
            log.warn("{} 요청에 Authorization 헤더가 존재하지 않습니다.", path);
            return Mono.error(new GatewayException(ResponseCode.ACCESS_TOKEN_NOT_FOUND));
        }

        if (!jwtUtil.validateToken(token)) {
            log.warn("{} 요청의 토큰이 유효하지 않습니다.", path);
            return Mono.error(new GatewayException(ResponseCode.JWT_NOT_VALID));
        }

        Claims claims;
        try {
            claims = jwtUtil.parseClaims(token);
        } catch (Exception e) {
            log.error("토큰 파싱 중 오류 발생: {}", e.getMessage());
            return Mono.error(new GatewayException(ResponseCode.JWT_NOT_VALID));
        }

        String tokenId = claims.getId();

        return isTokenBlacklisted(tokenId)
                .flatMap(isBlacklisted -> {
                    if (isBlacklisted) {
                        log.warn("블랙리스트에 등록된 토큰입니다: {}", tokenId);
                        return Mono.error(new GatewayException(ResponseCode.JWT_NOT_VALID));
                    }

                    ServerWebExchange mutatedExchange = addClaimsToRequestHeaders(exchange, claims);
                    log.info("{} 요청 인증에 성공하였습니다!", path);
                    return chain.filter(mutatedExchange);
                });
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.contains(path);
    }

    private Mono<Void> handleLogout(String token, GatewayFilterChain chain, ServerWebExchange exchange) {
        if (token == null || !jwtUtil.validateToken(token)) {
            log.warn("로그아웃 요청의 토큰이 유효하지 않음");
            return chain.filter(exchange);
        }

        try {
            Claims claims = jwtUtil.parseClaims(token);
            String tokenId = claims.getId();
            long ttl = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;

            String blacklistKey = BLACKLIST_PREFIX + tokenId;

            return reactiveRedisTemplate.opsForValue()
                    .set(blacklistKey, tokenId, Duration.ofSeconds(ttl))
                    .flatMap(success -> {
                        if (success) {
                            log.info("블랙리스트 등록 완료: {}", blacklistKey);
                        }
                        return chain.filter(exchange);
                    });
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage());
            return chain.filter(exchange);
        }
    }

    private Mono<Boolean> isTokenBlacklisted(String tokenId) {
        return reactiveRedisTemplate.hasKey(BLACKLIST_PREFIX + tokenId)
                .onErrorResume(e -> {
                    log.error("Redis 조회 중 오류: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    private ServerWebExchange addClaimsToRequestHeaders(ServerWebExchange exchange, Claims claims) {
        return exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Id", claims.getSubject())
                        .header("X-Email", claims.get("email", String.class))
                        .header("X-Role", claims.get("role", String.class)))
                .build();
    }
}