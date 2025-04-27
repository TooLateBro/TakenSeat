package com.taken_seat.auth_service.unit.mileage;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.mileage.MileageMapper;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.application.service.mileage.MileageServiceImpl;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.mileage.MileageQueryRepository;
import com.taken_seat.auth_service.domain.repository.mileage.MileageRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.presentation.dto.mileage.UserMileageRequestDto;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.customException.MileageException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MileageServiceTest {

    @Mock
    private MileageRepository mileageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MileageMapper mileageMapper;

    @Mock
    private MileageQueryRepository mileageQueryRepository;

    @InjectMocks
    private MileageServiceImpl mileageService;

    private User user;
    private Mileage mileage;
    private AuthenticatedUser authenticatedUser;

    private UUID userId;
    private UUID mileageId;
    private LocalDateTime updatedAt;

    @BeforeEach
    public void setUp() {
        updatedAt = LocalDateTime.parse("2025-12-31T23:59:59");
        user = User.create(
                "testuser1","test@test.com","010-1111-1111"
                ,"testPassword1!", Role.ADMIN
        );
        mileage = Mileage.create(
                user, 30000
        );

        authenticatedUser = new AuthenticatedUser(userId, "test@gmail.com", "MASTER");
    }

    @Test
    @DisplayName("마일리지 생성 성공 테스트")
    public void createMileageSuccess() {
        userId = UUID.randomUUID();
        UserMileageRequestDto mileageRequestDto = new UserMileageRequestDto(30000);

        User user = mock(User.class);
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

        UserMileageResponseDto mappedDto = new UserMileageResponseDto(30000, updatedAt);
        when(mileageMapper.userToUserMileageResponseDto(any(Mileage.class))).thenReturn(mappedDto);

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

    @Test
    @DisplayName("마일리지 단건 조회 성공 테스트")
    public void getMileageUserSuccess() {
        mileageId = UUID.randomUUID();

        Mileage mockMileage = Mockito.mock(Mileage.class);

        when(mileageRepository.findByIdAndDeletedAtIsNull(mileageId)).thenReturn(Optional.of(mockMileage));

        UserMileageResponseDto mappedDto = new UserMileageResponseDto(30000, updatedAt);
        when(mileageMapper.userToUserMileageResponseDto(mockMileage)).thenReturn(mappedDto);

        UserMileageResponseDto responseDto = mileageService.getMileageUser(mileageId);

        assertNotNull(responseDto);
    }


    @Test
    @DisplayName("마일리지 단건 조회 실패 테스트 - 마일리지 없음")
    public void getMileageUserFail_MileageNotFound() {
        mileageId = UUID.randomUUID();

        when(mileageRepository.findByIdAndDeletedAtIsNull(mileageId)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> mileageService.getMileageUser(mileageId));

        assertEquals(ResponseCode.MILEAGE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("마일리지 히스토리 조회 성공 테스트")
    public void getMileageHistoryUserSuccess() {
        userId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        List<Mileage> mileages = List.of(mileage);
        Page<Mileage> mileagePage = new PageImpl<>(mileages);

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));
        when(mileageRepository.findMileageByUserIdAndDeletedAtIsNull(eq(user.getId()), any(Pageable.class)))
                .thenReturn(mileagePage);

        PageResponseDto<UserMileageResponseDto> result = mileageService.getMileageHistoryUser(userId, page, size);

        assertNotNull(result);
    }
    @Test
    @DisplayName("마일리지 히스토리 조회 실패 테스트 - 유저 없음")
    public void getMileageHistoryUserFail_UserNotFound() {
        userId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> mileageService.getMileageHistoryUser(userId, page, size));

        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("마일리지 전체 조회 성공 테스트")
    public void searchMileageUserSuccess() {
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        List<Mileage> mileageList = List.of(mileage);
        Page<Mileage> mileagePage = new PageImpl<>(mileageList, pageable, mileageList.size());

        when(mileageQueryRepository.findAllByDeletedAtIsNull(null, null, pageable)).thenReturn(mileagePage);

        PageResponseDto<UserMileageResponseDto> result = mileageService.searchMileageUser(null, null, page, size);

        assertNotNull(result);
    }
    @Test
    @DisplayName("마일리지 전체 조회 실패 테스트 - 마일리지 없음")
    public void searchMileageUserFail_MileageNotFound() {
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Mileage> mileagePage =  new PageImpl<>(List.of(), pageable, 0);

        when(mileageQueryRepository.findAllByDeletedAtIsNull(null, null, pageable)).thenReturn(mileagePage);

        MileageException exception = assertThrows(MileageException.class, () ->
                mileageService.searchMileageUser(null, null, page, size));

        assertEquals(ResponseCode.MILEAGE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("마일리지 수정 성공 테스트")
    public void updateMileageUserSuccess() {
        mileageId = UUID.randomUUID();

        when(mileageRepository.findByIdAndDeletedAtIsNull(mileageId)).thenReturn(Optional.of(mileage));

        UserMileageRequestDto requestDto = new UserMileageRequestDto(40000);
        UserMileageResponseDto mappedDto = new UserMileageResponseDto(400000, updatedAt);
        when(mileageMapper.userToUserMileageResponseDto(mileage)).thenReturn(mappedDto);
        UserMileageResponseDto responseDto = mileageService.updateMileageUser(mileageId, authenticatedUser, requestDto.toDto());

        assertNotNull(responseDto);
        assertNotNull(mappedDto);
    }
    @Test
    @DisplayName("마일리지 수정 실패 테스트 - 마일리지 없음")
    public void updateMileageUserFail_MileageNotFound() {
        mileageId = UUID.randomUUID();

        when(mileageRepository.findByIdAndDeletedAtIsNull(mileageId)).thenReturn(Optional.empty());
        UserMileageRequestDto requestDto = new UserMileageRequestDto(40000);
        MileageException exception = assertThrows(MileageException.class, () ->
                mileageService.updateMileageUser(mileageId, authenticatedUser, requestDto.toDto()));

        assertEquals(ResponseCode.MILEAGE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("마일리지 삭제 성공 테스트")
    public void deleteMileageUserSuccess() {
        mileageId = UUID.randomUUID();

        when(mileageRepository.findByIdAndDeletedAtIsNull(mileageId)).thenReturn(Optional.of(mileage));

        mileageService.deleteMileageUser(mileageId, authenticatedUser);

        assertNotNull(mileage.getDeletedAt());
    }
    @Test
    @DisplayName("마일리지 삭제 실패 테스트 - 마일리지 없음")
    public void deleteMileageUserFail_MileageNotFound() {
        mileageId = UUID.randomUUID();

        when(mileageRepository.findByIdAndDeletedAtIsNull(mileageId)).thenReturn(Optional.empty());

        MileageException exception = assertThrows(MileageException.class, () ->
                mileageService.deleteMileageUser(mileageId, authenticatedUser));

        assertEquals(ResponseCode.MILEAGE_NOT_FOUND, exception.getErrorCode());
    }
}
