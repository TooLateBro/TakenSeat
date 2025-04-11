package com.taken_seat.common_service.component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.hibernate.type.SerializationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericJsonDeserializer<T> implements Deserializer<T> {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private Class<T> targetType;

	public GenericJsonDeserializer() {
	}

	public GenericJsonDeserializer(Class<T> targetType) {
		this.targetType = targetType;
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		if (configs != null && configs.containsKey("value.deserializer.type")) {
			try {
				this.targetType = (Class<T>)Class.forName((String)configs.get("value.deserializer.type"));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to configure deserializer type", e);
			}
		}
	}

	@Override
	public T deserialize(String topic, byte[] data) {
		if (data == null)
			return null;
		try {
			String json = new String(data, StandardCharsets.UTF_8);
			log.debug("Deserializing message from topic {}: {}", topic, json);

			if (targetType == null) {
				return (T)objectMapper.readValue(data, Object.class);
			}
			return objectMapper.readValue(data, targetType);
		} catch (Exception e) {
			log.error("Error deserializing message: {}", e.getMessage());
			throw new SerializationException("Error deserializing JSON message", e);
		}
	}

	@Override
	public void close() {
		// 리소스 정리가 필요한 경우 구현
	}
}