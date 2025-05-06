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

import com.taken_seat.common_service.message.BookingCompletedMessage;
import com.taken_seat.performance_service.performance.infrastructure.kafka.producer.SeatStatusChangedEvent;
import com.taken_seat.performance_service.recommend.infrastructure.kafka.dto.RecommendRequestMessage;
import com.taken_seat.performance_service.recommend.infrastructure.kafka.dto.UserSnapshotEvent;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

	@Value("${performance.kafka.dlq.retry-interval-ms}")
	private long retryIntervalMs;

	@Value("${performance.kafka.dlq.max-attempts}")
	private long maxAttempts;

	@Value("${common.kafka.bootstrap-servers}")
	private String bootstrapServers;

	private static final String TOPIC_SEAT_STATUS_DLQ = "seat-status-dlq-topic";
	private static final String GROUP_PERFORMANCE_SERVICE = "performance-service-group";
	private static final String GROUP_PERFORMANCE_RECOMMEND = "performance-recommend";
	private static final String GROUP_RECOMMEND_REQUEST = "recommend-request";

	/**
	 * 공통 ConsumerProps 생성 메서드
	 * @param groupId Kafka consumer groupId
	 * @param valueType Kafka 메시지 VALUE_DEFAULT_TYPE
	 * ConsumerFactory 빈 생성 시, buildCommonConsumerProps("그룹 ID", "DTO 클래스명") 호출로 대체
	 */
	private Map<String, Object> buildCommonConsumerProps(String groupId, String valueType) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType);
		return props;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SeatStatusChangedEvent>
	performanceKafkaListenerContainerFactory(
		ConsumerFactory<String, SeatStatusChangedEvent> seatStatusConsumerFactory,
		KafkaTemplate<String, SeatStatusChangedEvent> seatStatusKafkaTemplate
	) {

		ConcurrentKafkaListenerContainerFactory<String, SeatStatusChangedEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(seatStatusConsumerFactory);
		factory.setCommonErrorHandler(
			new DefaultErrorHandler(
				new DeadLetterPublishingRecoverer(
					seatStatusKafkaTemplate,
					(record, ex) -> new TopicPartition(TOPIC_SEAT_STATUS_DLQ, record.partition())
				),
				new FixedBackOff(retryIntervalMs, maxAttempts)
			)
		);
		return factory;
	}

	@Bean
	public ConsumerFactory<String, SeatStatusChangedEvent> seatStatusConsumerFactory() {
		String type = SeatStatusChangedEvent.class.getName();
		return new DefaultKafkaConsumerFactory<>(buildCommonConsumerProps(GROUP_PERFORMANCE_SERVICE, type));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, BookingCompletedMessage>
	bookingKafkaListenerContainerFactory(
		ConsumerFactory<String, BookingCompletedMessage> bookingConsumerFactory
	) {

		ConcurrentKafkaListenerContainerFactory<String, BookingCompletedMessage> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(bookingConsumerFactory);
		return factory;
	}

	@Bean
	public ConsumerFactory<String, BookingCompletedMessage> bookingConsumerFactory() {
		String type = BookingCompletedMessage.class.getName();
		return new DefaultKafkaConsumerFactory<>(buildCommonConsumerProps(GROUP_PERFORMANCE_RECOMMEND, type));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, RecommendRequestMessage>
	recommendRequestListenerContainerFactory(
		ConsumerFactory<String, RecommendRequestMessage> consumerFactory
	) {

		ConcurrentKafkaListenerContainerFactory<String, RecommendRequestMessage> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		return factory;
	}

	@Bean
	public ConsumerFactory<String, RecommendRequestMessage> recommendRequestConsumerFactory() {
		String type = RecommendRequestMessage.class.getName();
		return new DefaultKafkaConsumerFactory<>(buildCommonConsumerProps(GROUP_RECOMMEND_REQUEST, type));
	}

	@Bean
	public ConsumerFactory<String, UserSnapshotEvent> userSnapshotConsumerFactory() {
		String type = UserSnapshotEvent.class.getName();
		return new DefaultKafkaConsumerFactory<>(
			buildCommonConsumerProps("user-snapshot-group", type)
		);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, UserSnapshotEvent>
	userSnapshotListenerContainerFactory(
		ConsumerFactory<String, UserSnapshotEvent> userSnapshotConsumerFactory
	) {
		var factory = new ConcurrentKafkaListenerContainerFactory<String, UserSnapshotEvent>();
		factory.setConsumerFactory(userSnapshotConsumerFactory);
		return factory;
	}
}