package com.taken_seat.auth_service.presentation.dto.mileage;

import com.taken_seat.auth_service.application.dto.mileage.UserMileageDto;

public record UserMileageRequestDto(
        Integer mileage
) {
    public UserMileageDto toDto() {
        return UserMileageDto.create(this.mileage);
    }
}
