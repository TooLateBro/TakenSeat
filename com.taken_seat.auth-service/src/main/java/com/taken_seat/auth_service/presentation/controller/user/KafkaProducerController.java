package com.taken_seat.auth_service.presentation.controller.user;

import com.taken_seat.auth_service.application.service.user.KafkaProducerService;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class KafkaProducerController {

    private final KafkaProducerService kafkaProducerService;

    public KafkaProducerController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/send")
    public void sendUserCoupon(@RequestBody KafkaUserInfoMessage message){
        kafkaProducerService.sendUserCoupon(message);
    }
}
