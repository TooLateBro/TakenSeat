package com.taken_seat.gateway_service.filter;

import com.taken_seat.gateway_service.exception.customException.GatewayException;
import com.taken_seat.gateway_service.exception.enums.ResponseCode;
import com.taken_seat.gateway_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final WebClient.Builder webClientBuilder;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate, WebClient.Builder webClientBuilder) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.info("{} {} 요청이 들어왔습니다.", method, path);

        String token = jwtUtil.extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));

        if (path.equals("/api/v1/auths/logout")) {
            return handleLogout(token, exchange, chain);
        }

        if (isExcludedPath(path)) {
            log.info("{} 는 인증이 필요하지 않습니다.", path);
            return chain.filter(exchange);
        }

        log.info("{} 요청의 JWT 토큰 검증을 시작합니다.", path);

        // 현재 토큰 유효하지 않은 경우 새 accessToken 요청
        if (token == null || jwtUtil.isTokenInvalid(token)) {
            log.warn("{} 요청의 토큰이 유효하지 않아 새로운 token 을 요청합니다.", path);
            return newAccessToken(exchange, token)
                    .flatMap(mutatedExchange -> chain.filter(mutatedExchange))
                    .onErrorResume(e -> {
                        log.error("새 token 발급 실패: {}", e.getMessage());
                        return Mono.error(new GatewayException(ResponseCode.JWT_NOT_VALID));
                    });
        }

        try {
            // 블랙리스트 검사
            if (isBlackListToken(token)) {
                log.warn("{} 토큰이 블랙리스트입니다.", path);
                throw new GatewayException(ResponseCode.JWT_NOT_VALID);
            }

            Claims claims = jwtUtil.parseClaims(token);

            ServerWebExchange mutatedExchange = addClaimsToRequestHeaders(exchange, claims);

            log.info("{} 요청 인증에 성공하였습니다!", path);
            return chain.filter(mutatedExchange);

        } catch (GatewayException e) {
            log.error("인증 실패: {}", e.getMessage());
            return Mono.error(e);
        } catch (Exception e) {
            log.error("인증 처리 중 오류 발생: {}", e.getMessage());
            return Mono.error(new GatewayException(ResponseCode.JWT_NOT_VALID));
        }
    }

    private Mono<Void> handleLogout(String token, ServerWebExchange exchange, GatewayFilterChain chain) {
        if (token == null || jwtUtil.isTokenInvalid(token)) {
            log.warn("로그아웃 요청의 토큰이 유효하지 않음");
        } else {
            String tokenId = jwtUtil.parseClaims(token).getId();
            addTokenToBlacklist(tokenId);
            log.info("로그아웃 요청 토큰 블랙리스트 등록 완료");
        }
        return chain.filter(exchange); // 이후 컨트롤러에서 refreshToken 삭제
    }

    private boolean isExcludedPath(String path) {
        return  path.startsWith("/api/v1/auths/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui");
    }
    private Mono<ServerWebExchange> newAccessToken(ServerWebExchange exchange, String token) {
        return webClientBuilder.build()
                .post()
                .uri("lb://auth-service/api/v1/auths/receive/newAccessToken")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(newAccessToken -> {
                    Claims claims = jwtUtil.parseClaims(newAccessToken);
                    return Mono.just(addClaimsToRequestHeaders(exchange, claims));
                });
    }

    private boolean isBlackListToken(String token) {
        try {
            String tokenId = jwtUtil.parseClaims(token).getId();
            log.debug("블랙 리스트 확인 중....");

            Boolean isBlacklisted = redisTemplate.hasKey("BLACKLIST : " + tokenId);

            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.info("토큰 ID {}가 블랙리스트에 등록되어 있습니다.", tokenId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.warn("블랙리스트 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    private void addTokenToBlacklist(String tokenId) {
        redisTemplate.opsForValue().set(
                "BLACKLIST : " + tokenId, tokenId, 1, TimeUnit.HOURS
        );
    }

    private ServerWebExchange addClaimsToRequestHeaders(ServerWebExchange exchange, Claims claims) {
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userId)
                .header("X-Email", email)
                .header("X-Role", role)
                .build();

        return exchange.mutate().request(mutatedRequest).build();
    }
}