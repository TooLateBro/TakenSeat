package com.taken_seat.queue_service.infrastructure.messaging;

import com.taken_seat.common_service.message.QueueEnterMessage;
import com.taken_seat.queue_service.application.kafka.QueueKafkaConsumer;
import com.taken_seat.queue_service.application.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueKafkaConsumerImpl implements QueueKafkaConsumer {
    private final QueueService queueService;

    @Override
    @KafkaListener(topics = "${kafka.topic.booking-request}", groupId = "${kafka.consumer.group-id}")
    public void acceptBookingRequestEvent(QueueEnterMessage message) {
        queueService.sendToBooking(message);
    }
}
