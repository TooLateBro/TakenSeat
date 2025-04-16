package com.taken_seat.queue_service.infrastructure.messaging;

import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.queue_service.application.kafka.QueueKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueKafkaProducerImpl implements QueueKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.queue-request}")
    private String topic;

    @Override
    public void sendBookingRequestEvent(BookingRequestMessage message) {
        //공연 Id를 키로 사용하여 동일 파티션 내에서 유저 순서 보장
        kafkaTemplate.send(topic, message.getPerformanceId().toString(), message);
    }
}
