package com.taken_seat.performance_service.common.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.taken_seat.performance_service.performance.infrastructure.kafka.producer.SeatStatusChangedEvent;
import com.taken_seat.performance_service.recommend.infrastructure.kafka.dto.RecommendRequestMessage;

@Configuration
public class KafkaProducerConfig {

	@Value("${common.kafka.bootstrap-servers}")
	private String bootstrapServers;

	private Map<String, Object> buildCommonProducerProps() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
		return props;
	}

	@Bean
	public ProducerFactory<String, SeatStatusChangedEvent> seatStatusProducerFactory() {
		return new DefaultKafkaProducerFactory<>(buildCommonProducerProps());
	}

	@Bean
	public KafkaTemplate<String, SeatStatusChangedEvent> seatStatusKafkaTemplate() {
		return new KafkaTemplate<>(seatStatusProducerFactory());
	}

	@Bean
	public ProducerFactory<String, RecommendRequestMessage> recommendRequestProducerFactory() {
		return new DefaultKafkaProducerFactory<>(buildCommonProducerProps());
	}

	@Bean
	public KafkaTemplate<String, RecommendRequestMessage> recommendRequestKafkaTemplate() {
		return new KafkaTemplate<>(recommendRequestProducerFactory());
	}
}
