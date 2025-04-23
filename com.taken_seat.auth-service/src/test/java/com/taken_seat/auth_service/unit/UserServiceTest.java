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

    @Test
    @DisplayName("유저 단건 상세 조회 성공 테스트")
    public void getUserDetailsSuccess() {
        userId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        User mockedUser = Mockito.mock(User.class);

        when(mockedUser.getId()).thenReturn(userId);
        when(mockedUser.getUsername()).thenReturn("testuser1");
        when(mockedUser.getMileages()).thenReturn(List.of());

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(mockedUser));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserCoupon> userCouponPage = new PageImpl<>(List.of());

        when(userCouponRepository.findCouponIdByUserIdAndIsActiveTrue(any(UUID.class), eq(pageable)))
                .thenReturn(userCouponPage);

        UserInfoResponseDto result = userService.getUserDetails(userId, page, size);

        assertNotNull(result);
        assertEquals("testuser1", result.username());
    }
    @Test
    @DisplayName("유저 단건 상세 조회 실패 테스트")
    public void getUserDetailsFail() {
        userId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () ->
                userService.getUserDetails(userId, page, size)
        );
        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 전체 조회 성공 테스트")
    public void searchUserSuccess() {
        int page = 0;
        int size = 10;

        User mockedUser = Mockito.mock(User.class);
        when(mockedUser.getUserCoupons()).thenReturn(List.of());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<User> userList = List.of(mockedUser);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());
        when(userQueryRepository.findAllByDeletedAtIsNull(null, null, pageable)).thenReturn(userPage);

        PageResponseDto<UserInfoResponseDto> result = userService.searchUser(null, null, page, size);

        assertNotNull(result);
    }
    @Test
    @DisplayName("유저 전체 조회 실패 테스트 - 유저 없음")
    public void searchUserFail_NoUserFound() {
        int page = 0;
        int size = 10;
        String username = "testuser1";

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> emptyUserPage = new PageImpl<>(List.of(), pageable, 0);
        when(userQueryRepository.findAllByDeletedAtIsNull(username, null, pageable)).thenReturn(emptyUserPage);

        AuthException exception = assertThrows(AuthException.class, () ->
                userService.searchUser(username, null, page, size));

        assertNotNull(exception);
    }
}