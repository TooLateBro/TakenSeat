package com.taken_seat.auth_service.application.dto.mileage;

public record UserMileageDto(
        Integer mileage
) {

    public static UserMileageDto create(Integer mileage) {
        return new UserMileageDto(mileage);
    }
}
