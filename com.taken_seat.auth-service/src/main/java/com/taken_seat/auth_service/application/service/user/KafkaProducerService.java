package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;

    private static final String REQUEST_TOPIC = "Issuance-of-coupons";
    private static final String REQUEST_KEY = "Partitions-of-coupons";

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate, UserRepository userRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
    }

    public void sendUserCoupon(KafkaUserInfoMessage message) {
        User user = userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        kafkaTemplate.send(REQUEST_TOPIC, REQUEST_KEY, message);
    }
}