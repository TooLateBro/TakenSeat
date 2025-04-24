package com.taken_seat.auth_service.application.dto.mileage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserMileageResponseDto(
        UUID userId,
        UUID mileageId,
        Integer mileage,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {

    public static UserMileageResponseDto of(Mileage mileage) {
        return new UserMileageResponseDto(
                mileage.getUser().getId(), mileage.getId(),
                mileage.getMileage(), mileage.getUpdatedAt()
        );
    }
}
