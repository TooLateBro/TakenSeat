package com.taken_seat.auth_service.presentation.dto.mileage;

import com.taken_seat.auth_service.application.dto.mileage.UserMileageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMileageRequestDto {

    private Integer mileage;

    public UserMileageDto toDto() {
        return UserMileageDto.create(this.mileage);
    }
}
