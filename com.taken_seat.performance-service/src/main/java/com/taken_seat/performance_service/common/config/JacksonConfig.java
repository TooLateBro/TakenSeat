package com.taken_seat.performance_service.common.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Jackson 직렬화/역직렬화 설정 클래스
 * - LocalDateTime 타입의 필드를 JSON으로 변환할 때,
 *   ISO 기본 포맷이 아닌 사용자 정의 포맷("yyyy-MM-dd HH:mm:ss")으로 직렬화 및 역질렬화
 * - 각 DTO 클래스에서 중복되는 @JsonFormat 어노테이션을 제거하고,
 *   전역 설정을 통해 일관된 형식의 날짜 문자열 유지
 * - WRITE_DATES_AS_TIMESTAMPS 옵션을 꺼서 타임스탬프 대신 문자열 형태로 출력되도록 설정
 */
@Configuration
public class JacksonConfig {

	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		JavaTimeModule module = new JavaTimeModule();
		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));

		mapper.registerModule(module);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return mapper;
	}
}
