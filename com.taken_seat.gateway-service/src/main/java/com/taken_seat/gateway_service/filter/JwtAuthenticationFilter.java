package com.taken_seat.gateway_service.filter;

import com.taken_seat.gateway_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
// GlobalFilter 인터페이스를 구현하여 모든 요청에 필터 적용
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Mono 는 단일 응답(0~1개의 데이터)을 비동기적으로 처리하며, 여기서는 요청 처리 결과를 반환
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 요청 경로를 가져옴
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/api/v1/auths/")) {
            return chain.filter(exchange); // 다음 필터로 요청을 전달
        }
        // 만약 /api/v1/users/ 로 요청의 헤더값에 token 이 담겨오면 해당 token 을 검사해서 헤더에 데이터를 담아서 반환
        // 요청에서 JWT 토큰을 추출
        try {
            String token = jwtUtil.extractToken(exchange);

        // 토큰이 없거나 유효하지 않으면 401 Unauthorized 응답 반환
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("인증이 올바르지 않습니다.");
        }

        // 유효한 토큰에서 페이로드 파싱
        Claims claims = jwtUtil.parseClaims(token);
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        // 기존 ServerWebExchange 객체를 수정하여 요청 헤더에 사용자 정보를 추가
        // mutate()는 원본 객체를 변경하지 않고 새로운 객체를 생성
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(
                        builder -> builder // 요청 빌더를 사용해 헤더 설정
                                .header("X-User-Id", userId)
                                .header("X-Email", email)
                                .header("X-Role", role))
                .build();
        // 수정된 요청을 다음 필터 또는 라우트로 전달
            return chain.filter(mutatedExchange);

        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        } catch (Exception e) {
            return Mono.error(new IllegalArgumentException("인증 정보가 올바르지 않습니다."));
        }
    }
}