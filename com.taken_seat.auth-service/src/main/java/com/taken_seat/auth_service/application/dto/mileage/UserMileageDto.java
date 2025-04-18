package com.taken_seat.auth_service.application.dto.mileage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UserMileageDto {

    private Integer mileage;

    public static UserMileageDto create(Integer count) {
        return UserMileageDto.builder().mileage(count).build();
    }
}
