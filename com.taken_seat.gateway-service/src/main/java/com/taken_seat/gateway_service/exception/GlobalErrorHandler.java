package com.taken_seat.gateway_service.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taken_seat.gateway_service.exception.customException.BaseException;
import com.taken_seat.gateway_service.exception.enums.ResponseCode;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorAttributes = new HashMap<>();

        // 커스텀 예외인 경우
        if (ex instanceof BaseException baseEx) {
            ResponseCode errorCode = baseEx.getErrorCode(); // BaseException에서 errorCode 가져오기
            exchange.getResponse().setStatusCode(errorCode.getStatus()); // 응답 상태코드 설정

            // 응답 JSON에 들어갈 내용
            errorAttributes.put("code", errorCode.getCode());
            errorAttributes.put("message", errorCode.getMessage());
        } else {
            // 예상하지 못한 예외
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            errorAttributes.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorAttributes.put("message", ex.getMessage());
        }
        // 요청 경로 포함
        errorAttributes.put("path", exchange.getRequest().getPath().value());

        DataBuffer buffer; // Spring WebFlux 에서 응답(또는 요청)의 바이트 데이터를 담는 객체

        byte[] bytes = null;
        try {
            // Map을 JSON 바이트 배열로 변환
            bytes = objectMapper.writeValueAsBytes(errorAttributes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // JSON 응답 내용을 DataBuffer로 래핑
        buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        // WebFlux 방식으로 비동기 응답 반환
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

