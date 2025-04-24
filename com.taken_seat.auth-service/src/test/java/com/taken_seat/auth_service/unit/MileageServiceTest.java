package com.taken_seat.auth_service.unit;

import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.application.service.mileage.MileageServiceImpl;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.mileage.MileageQueryRepository;
import com.taken_seat.auth_service.domain.repository.mileage.MileageRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.vo.Role;
import com.taken_seat.auth_service.presentation.dto.mileage.UserMileageRequestDto;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MileageServiceTest {

    @Mock
    private MileageRepository mileageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MileageQueryRepository mileageQueryRepository;

    @InjectMocks
    private MileageServiceImpl mileageService;

    private User user;
    private UUID userId;
    private UUID mileageId;

    @Test
    @DisplayName("마일리지 생성 성공 테스트")
    public void createMileageSuccess() {
        userId = UUID.randomUUID();
        user = User.create(
                "testuser1","test@test.com","010-1111-1111"
                ,"testPassword1!", Role.ADMIN
        );
        UserMileageRequestDto mileageRequestDto = new UserMileageRequestDto(
                30000
        );
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));
        UserMileageResponseDto responseDto = mileageService.createMileageUser(userId, mileageRequestDto.toDto());

        assertNotNull(responseDto);
    }
    @Test
    @DisplayName("마일리지 생성 실패 테스트 - 유저 없음")
    public void createMileageFail_UserNotFound() {
        userId = UUID.randomUUID();

        UserMileageRequestDto mileageRequestDto = new UserMileageRequestDto(
                30000
        );
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> mileageService.createMileageUser(userId, mileageRequestDto.toDto()));

        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}
