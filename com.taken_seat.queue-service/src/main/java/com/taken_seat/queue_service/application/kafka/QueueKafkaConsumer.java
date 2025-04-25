package com.taken_seat.queue_service.application.kafka;

import com.taken_seat.common_service.message.QueueEnterMessage;

public interface QueueKafkaConsumer {
    void acceptBookingRequestEvent(QueueEnterMessage message);
}
