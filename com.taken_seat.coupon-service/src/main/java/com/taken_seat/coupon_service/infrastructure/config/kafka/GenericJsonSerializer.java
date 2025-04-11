package com.taken_seat.coupon_service.infrastructure.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class GenericJsonSerializer<T> implements Serializer<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> targetType;

    public GenericJsonSerializer() {
    }

    public GenericJsonSerializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (configs != null && configs.containsKey("value.serializer.type")) {
            try {
                this.targetType = (Class<T>) Class.forName((String) configs.get("value.serializer.type"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to configure serializer type", e);
            }
        }
    }

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) return null;
        try {
            byte[] result = objectMapper.writeValueAsBytes(data);
            log.debug("Serializing message for topic {}: {}", topic, data);
            return result;
        } catch (JsonProcessingException e) {
            log.error("Error serializing data: {}", e.getMessage());
            throw new SerializationException("Error serializing value", e);
        }
    }

    @Override
    public void close() {
        // 리소스 정리가 필요한 경우 구현
    }
}