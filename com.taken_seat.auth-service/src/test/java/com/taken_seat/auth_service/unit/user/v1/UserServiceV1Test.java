package com.taken_seat.auth_service.unit.user.v1;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserUpdateDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserDetailsResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.v1.UserMapper;
import com.taken_seat.auth_service.application.service.user.v1.UserServiceV1Impl;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import com.taken_seat.auth_service.infrastructure.persistence.user.UserQueryRepositoryImpl;
import com.taken_seat.auth_service.presentation.dto.user.UserUpdateRequestDto;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.customException.CouponException;
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
public class UserServiceV1Test {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserQueryRepositoryImpl userQueryRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceV1Impl userServiceV1;

    private User user;

    private UUID userId;
    private UUID couponId;


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
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

        UserInfoResponseDtoV1 mappedDto = new UserInfoResponseDtoV1(
                user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getRole());
        when(userMapper.userToUserInfoResponseDto(user)).thenReturn(mappedDto);

        UserInfoResponseDtoV1 resultV1 = userServiceV1.getUser(userId);

        assertNotNull(resultV1);
    }


    @Test
    @DisplayName("유저 단건 조회 실패 테스트")
    public void getUserFail() {
        userId = UUID.randomUUID();

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> userServiceV1.getUser(userId));

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

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(mockedUser));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserCoupon> userCouponPage = new PageImpl<>(List.of());
        when(userCouponRepository.findCouponIdByUserIdAndIsActiveTrue(any(UUID.class), eq(pageable)))
                .thenReturn(userCouponPage);

        UserDetailsResponseDtoV1 mockedDto = new UserDetailsResponseDtoV1(
                mockedUser.getId(), mockedUser.getUsername(), null, null, null, null, null
        );
        when(userMapper.userToUserInfoDetailsResponseDto(any(User.class), eq(userCouponPage)))
                .thenReturn(mockedDto);
        UserDetailsResponseDtoV1 resultV1 = userServiceV1.getUserDetails(userId, page, size);

        assertNotNull(resultV1);
    }


    @Test
    @DisplayName("유저 단건 상세 조회 실패 테스트")
    public void getUserDetailsFail() {
        userId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () ->
                userServiceV1.getUserDetails(userId, page, size)
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

        PageResponseDto<UserDetailsResponseDtoV1> resultV1 = userServiceV1.searchUser(null, null, page, size);

        assertNotNull(resultV1);
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
                userServiceV1.searchUser(username, null, page, size));

        assertNotNull(exception);
    }

    @Test
    @DisplayName("유저 수정 성공 테스트")
    public void updateUserSuccess() {
        userId = UUID.randomUUID();
        String username = "updateuser1";
        String password = "updatePassword1!";
        String phone = "010-2222-2222";
        String email = "update@test.com";
        Role role = Role.CUSTOMER;

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

        UserInfoResponseDtoV1 mockedResponse = new UserInfoResponseDtoV1(
                user.getId(), user.getUsername(), user.getPhone(), user.getEmail(), null
        );
        when(userMapper.userToUserInfoResponseDto(user)).thenReturn(mockedResponse);

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, email, phone, password, role);
        when(userMapper.toDto(any(UserUpdateRequestDto.class))).thenReturn(userUpdateDto);

        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(
                username, password, phone, email, role
        );

        UserInfoResponseDtoV1 userInfoResponseDtoV1 = userServiceV1.updateUser(userId, userMapper.toDto(userUpdateRequestDto));

        assertNotNull(userInfoResponseDtoV1);
    }

    @Test
    @DisplayName("유저 수정 실패 테스트 - 유저 없음")
    public void updateUserFail_NoUserFound() {
        userId = UUID.randomUUID();
        String username = "updateuser1";
        String password = "updatePassword1!";
        String phone = "010-2222-2222";
        String email = "update@test.com";
        Role role = Role.CUSTOMER;

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());
        UserUpdateDto userUpdateDto = new UserUpdateDto(username, email, phone, password, role);
        when(userMapper.toDto(any(UserUpdateRequestDto.class))).thenReturn(userUpdateDto);
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(
                username, password, phone, email, role
        );

        AuthException exception = assertThrows(AuthException.class, () ->
                userServiceV1.updateUser(userId, userMapper.toDto(userUpdateRequestDto)));

        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("유저 삭제 성공 테스트")
    public void deleteUserSuccess() {
        userId = UUID.randomUUID();

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));
        userServiceV1.deleteUser(userId);

        assertNotNull(user.getDeletedAt());
    }
    @Test
    @DisplayName("유저 삭제 실패 테스트 - 유저 없음")
    public void deleteUserFail_NoUserFound() {
        userId = UUID.randomUUID();

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, ()->
                userServiceV1.deleteUser(userId));

        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("쿠폰 발급 성공 테스트")
    public void getCouponSuccess() {
        couponId = UUID.randomUUID();

        UserCoupon mockCoupon = Mockito.mock(UserCoupon.class);
        when(userCouponRepository.findByCouponId(couponId)).thenReturn(Optional.of(mockCoupon));

        String result = userServiceV1.getCoupon(couponId);

        assertTrue(result.contains("축하합니다!"));
        assertTrue(result.contains("수령에 성공했습니다!"));
    }
    @Test
    @DisplayName("쿠폰 발급 실패 테스트 - 쿠폰 발급 실패")
    public void getCouponFail_NoCouponFound() {
        couponId = UUID.randomUUID();

        when(userCouponRepository.findByCouponId(couponId)).thenReturn(Optional.empty());

        CouponException exception = assertThrows(CouponException.class, ()->
                userServiceV1.getCoupon(couponId));

        assertEquals(ResponseCode.COUPON_QUANTITY_EXCEPTION, exception.getErrorCode());
    }
}