package com.taken_seat.auth_service.presentation.dto.mileage;

import com.taken_seat.auth_service.application.dto.mileage.UserMileageDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserMileageRequestDto(
        @Schema(example = "0")
        Integer mileage
) {
    public UserMileageDto toDto() {
        return UserMileageDto.create(this.mileage);
    }
}
