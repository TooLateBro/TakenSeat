package com.taken_seat.auth_service.application.dto.mileage;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.presentation.dto.mileage.UserMileageRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

// 매핑되지 않은 필드가 있더라도 경고를 발생시키지 않고 무시
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface MileageMapper {

    UserMileageResponseDto userToUserMileageResponseDto(Mileage mileage);

    UserMileageDto toDto(UserMileageRequestDto dto);
}
