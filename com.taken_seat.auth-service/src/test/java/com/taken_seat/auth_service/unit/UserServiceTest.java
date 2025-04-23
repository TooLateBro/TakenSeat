package com.taken_seat.auth_service.unit;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.application.service.user.UserServiceImpl;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import com.taken_seat.auth_service.domain.vo.Role;
import com.taken_seat.auth_service.infrastructure.persistence.user.UserQueryRepositoryImpl;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserQueryRepositoryImpl userQueryRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserCoupon userCoupon;
    private Mileage mileage;

    private UUID userId;

    @BeforeEach
    public void setUp() {
        user = User.create(
                "testuser1","test@test.com","010-1111-1111"
                ,"testPassword1!", Role.ADMIN
        );
    }

    @Test
    @DisplayName("유저 단건 조회 성공 테스트")
    public void getUserSuccess() {
        userId = UUID.randomUUID();

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

        UserInfoResponseDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals("testuser1", result.username());
    }

    @Test
    @DisplayName("유저 단건 조회 실패 테스트")
    public void getUserFail() {
        userId = UUID.randomUUID();

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> userService.getUser(userId));

        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getErrorCode());
    }


}