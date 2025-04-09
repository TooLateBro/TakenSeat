package com.taken_seat.auth_service.application.dto.mileage;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import lombok.*;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMileageResponseDto {

    private UUID userId;
    private UUID mileageId;
    private Integer count;

    public static UserMileageResponseDto of(Mileage mileage) {
        return UserMileageResponseDto.builder()
                .userId(mileage.getUser().getId())
                .mileageId(mileage.getId())
                .count(mileage.getCount())
                .build();
    }
}
