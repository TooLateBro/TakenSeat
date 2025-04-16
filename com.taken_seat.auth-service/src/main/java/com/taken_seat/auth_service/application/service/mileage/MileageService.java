package com.taken_seat.auth_service.application.service.mileage;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageDto;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.common_service.dto.AuthenticatedUser;

import java.util.UUID;

public interface MileageService {

    UserMileageResponseDto createMileageUser(UUID userId, UserMileageDto dto);

    UserMileageResponseDto getMileageUser(UUID mileageId);

    PageResponseDto<UserMileageResponseDto> getMileageHistoryUser(UUID userId, int page, int size);

    PageResponseDto<UserMileageResponseDto> searchMileageUser(
            Integer startCount, Integer endCount, int page, int size);

    UserMileageResponseDto updateMileageUser(UUID mileageId, AuthenticatedUser authenticatedUser, UserMileageDto dto);

    void deleteMileageUser(UUID mileageId, AuthenticatedUser authenticatedUser);
}
