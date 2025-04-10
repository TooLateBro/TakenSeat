package com.taken_seat.auth_service.application.dto.mileage;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMileageResponseDto {

    private UUID userId;
    private UUID mileageId;
    private Integer count;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    public static UserMileageResponseDto of(Mileage mileage) {
        return UserMileageResponseDto.builder()
                .userId(mileage.getUser().getId())
                .mileageId(mileage.getId())
                .count(mileage.getCount())
                .updatedAt(mileage.getUpdatedAt())
                .build();
    }
}
