package com.taken_seat.performance_service.common.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.taken_seat.performance_service.performance.infrastructure.kafka.producer.SeatStatusChangedEvent;

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
		ConsumerFactory<String, SeatStatusChangedEvent> consumerFactory,
		KafkaTemplate<String, SeatStatusChangedEvent> kafkaTemplate
	) {
		ConcurrentKafkaListenerContainerFactory<String, SeatStatusChangedEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);

		factory.setCommonErrorHandler(new DefaultErrorHandler(
			new DeadLetterPublishingRecoverer(kafkaTemplate,
				(record, ex) -> new TopicPartition(
					"seat-status-dlq-topic", record.partition())),
			new FixedBackOff(0L, 3)
		));

		return factory;
	}
}
