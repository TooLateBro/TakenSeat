package com.taken_seat.performance_service.common.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.taken_seat.performance_service.performance.infrastructure.kafka.producer.SeatStatusChangedEvent;
import com.taken_seat.performance_service.recommend.infrastructure.kafka.dto.BookingCompletedMessage;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

	/**
	 * 재시도 간격(ms) — 예외 발생 후 다음 재시도를 하기까지 기다리는 시간
	 * 0 - 즉시 재시도
	 */
	@Value("${performance.kafka.dlq.retry-interval-ms}")
	private long retryIntervalMs;

	/**
	 * 최대 재시도 횟수 — 메시지 소비에 실패했을 때 몇 번까지 재시도할지 선택
	 * 3 - 최대 3번까지 처리 시도, 실패 시 DLQ로 이동
	 */
	@Value("${performance.kafka.dlq.max-attempts}")
	private long maxAttempts;

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SeatStatusChangedEvent> performanceKafkaListenerContainerFactory(
		ConsumerFactory<String, SeatStatusChangedEvent> seatStatusConsumerFactory,
		KafkaTemplate<String, SeatStatusChangedEvent> seatStatusKafkaTemplate
	) {
		ConcurrentKafkaListenerContainerFactory<String, SeatStatusChangedEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(seatStatusConsumerFactory);

		factory.setCommonErrorHandler(new DefaultErrorHandler(
			new DeadLetterPublishingRecoverer(seatStatusKafkaTemplate,
				(record, ex) -> new TopicPartition(
					"seat-status-dlq-topic", record.partition())),
			new FixedBackOff(retryIntervalMs, maxAttempts)
		));

		return factory;
	}

	@Bean
	public ConsumerFactory<String, SeatStatusChangedEvent> seatStatusConsumerFactory(
		@Value("${common.kafka.bootstrap-servers}") String bootstrapServers
	) {
		Map<String, Object> props = new HashMap<>();
		props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "performance-service-group");
		props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
			org.apache.kafka.common.serialization.StringDeserializer.class);
		props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
			org.springframework.kafka.support.serializer.JsonDeserializer.class);
		props.put(org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES, "*");
		props.put(
			JsonDeserializer.VALUE_DEFAULT_TYPE,
			"com.taken_seat.performance_service.performance.infrastructure.kafka.producer.SeatStatusChangedEvent");

		return new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, BookingCompletedMessage> bookingKafkaListenerContainerFactory(
		ConsumerFactory<String, BookingCompletedMessage> bookingConsumerFactory
	) {
		ConcurrentKafkaListenerContainerFactory<String, BookingCompletedMessage> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(bookingConsumerFactory);
		return factory;
	}

	@Bean
	public ConsumerFactory<String, BookingCompletedMessage> bookingConsumerFactory(
		@Value("${common.kafka.bootstrap-servers}") String bootstrapServers
	) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "performance-recommend");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, BookingCompletedMessage.class.getName());
		return new DefaultKafkaConsumerFactory<>(props);
	}
}
