package com.taken_seat.queue_service.application.kafka;


import com.taken_seat.common_service.message.BookingRequestMessage;

public interface QueueKafkaProducer {
    void sendBookingRequestEvent(BookingRequestMessage message);
}
