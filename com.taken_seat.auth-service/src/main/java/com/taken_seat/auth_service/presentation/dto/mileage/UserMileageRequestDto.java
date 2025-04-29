package com.taken_seat.auth_service.presentation.dto.mileage;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserMileageRequestDto(
        @Schema(example = "0")
        Integer mileage
) {}
