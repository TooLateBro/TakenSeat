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
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    // 인증 제외 경로 상수로 관리
    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/v1/auths/",
            "/v3/api-docs",
            "/swagger-ui"
    );
    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String token = jwtUtil.extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));

        log.info("{} {} 요청이 들어왔습니다.", method, path);

        if (path.equals("/api/v1/auths/logout")) {
            if (token == null || !jwtUtil.validateToken(token)) {
                log.warn("로그아웃 요청의 토큰이 유효하지 않음");
            }
            String tokenId = jwtUtil.parseClaims(token).getId();
            redisTemplate.opsForValue().set("BLACKLIST:" + tokenId, tokenId, 1, TimeUnit.HOURS);
            log.info("로그아웃 요청 토큰 블랙리스트 등록 완료");
            return chain.filter(exchange); // 이후 컨트롤러에서 refreshToken 삭제
        }
        // 인증 제외 경로 처리
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            log.info("{} 는 인증이 필요하지 않습니다.", path);
            return chain.filter(exchange);
        }

        log.info("{} 요청의 JWT 토큰 검증을 시작합니다.", path);

        try {
            // Authorization 헤더에서 토큰 추출
            if (token == null) {
                log.warn("{} 요청에 Authorization 헤더가 존재하지 않습니다.", path);
                throw new GatewayException(ResponseCode.ACCESS_TOKEN_NOT_FOUND);
            }
            if (!jwtUtil.validateToken(token)) {
                log.warn("{} 요청의 토큰이 유효하지 않습니다.", path);
                throw new GatewayException(ResponseCode.JWT_NOT_VALID);
            }
            if (isBlackListToken(token)) {
                log.warn("{} 토큰이 블랙리스트 입니다.", path);
                throw new GatewayException(ResponseCode.JWT_NOT_VALID);
            }

            // 토큰 검증 후 클레임 가져오기
            Claims claims = jwtUtil.parseClaims(token);
            ServerWebExchange mutatedExchange = addClaimsToRequestHeaders(exchange, claims);

            log.info("{} 요청 인증에 성공하였습니다!", path);
            return chain.filter(mutatedExchange);

        }catch (GatewayException e) {
            log.error("로그인을 해주세요!");
            return Mono.error(new GatewayException(ResponseCode.ACCESS_TOKEN_NOT_FOUND));
        }catch (Exception e) {
            log.error("인증 처리 중 오류 발생: {}", e.getMessage());
            return Mono.error(new GatewayException(ResponseCode.JWT_NOT_VALID));
        }
    }

    private ServerWebExchange addClaimsToRequestHeaders(ServerWebExchange exchange, Claims claims) {
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        return exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Id", userId)
                        .header("X-Email", email)
                        .header("X-Role", role))
                .build();
    }
    private boolean isBlackListToken(String token) {
        try {
            String tokenId = jwtUtil.parseClaims(token).getId(); // 토큰의 고유 id를 blackList 저장
            log.info("블랙 리스트 확인 중....");
            Boolean isBlacklisted = redisTemplate.hasKey("BLACKLIST:" + tokenId);
            log.info("유저의 토큰 : {} 값이 블랙 리스트에 등록 되어있습니다.", token);
            return Boolean.TRUE.equals(isBlacklisted);
        } catch (Exception e) {
            return false;
        }
    }
}