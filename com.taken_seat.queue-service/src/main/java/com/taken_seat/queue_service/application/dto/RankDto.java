package com.taken_seat.queue_service.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RankDto {
    @NotBlank(message = "token은 필수값입니다.")
    private String token;
}
