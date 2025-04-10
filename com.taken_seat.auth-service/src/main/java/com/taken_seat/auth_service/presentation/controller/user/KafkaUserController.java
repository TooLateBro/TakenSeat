package com.taken_seat.auth_service.presentation.controller.user;

import com.taken_seat.auth_service.application.dto.user.KafkaUserInfoMessage;
import com.taken_seat.auth_service.application.service.user.KafkaUserService;
import com.taken_seat.common_service.dto.ApiResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class KafkaUserController {

    private final KafkaUserService kafkaUserService;

    public KafkaUserController(KafkaUserService kafkaUserService) {
        this.kafkaUserService = kafkaUserService;
    }

    @GetMapping("/send")
    public ResponseEntity<ApiResponseData<KafkaUserInfoMessage>> sendUserCoupon(@RequestHeader("X-User-Id")UUID userId){
        KafkaUserInfoMessage coupon = kafkaUserService.sendUserCoupon(userId);

        return ResponseEntity.ok().body(ApiResponseData.success(coupon));
    }
}
