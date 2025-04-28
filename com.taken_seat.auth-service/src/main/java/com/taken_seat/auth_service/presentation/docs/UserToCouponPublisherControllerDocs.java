package com.taken_seat.auth_service.presentation.docs;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Kafka 이벤트", description = "쿠폰 선착순 발행 요청을 수행하는 Kafka Publisher 입니다.")
public interface UserToCouponPublisherControllerDocs {

    @PostMapping("/api/v1/users/send")
    @Operation(summary = "쿠폰 선착순 발행 요청", description = "쿠폰 선착순 발행 요청을 담당하는 Kafka Publisher API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 선착순 발행이 성공적으로 요청되었습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    void sendUserCoupon(@RequestBody KafkaUserInfoMessage message);
}
