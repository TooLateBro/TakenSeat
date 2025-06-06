package com.taken_seat.coupon_service.unit;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.coupon_service.application.dto.CouponMapper;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;
import com.taken_seat.coupon_service.application.service.CouponServiceImpl;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponQueryRepository;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import com.taken_seat.coupon_service.presentation.dto.CreateCouponRequestDto;
import com.taken_seat.coupon_service.presentation.dto.UpdateCouponRequestDto;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponQueryRepository couponQueryRepository;

    @Mock
    private CouponMapper couponMapper;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;


    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon coupon;
    private UUID couponId;
    private AuthenticatedUser authenticatedUser;
    private LocalDateTime expiredAt;

    private UUID userId = UUID.randomUUID();
    @BeforeEach
    public void setUp() {
        expiredAt = LocalDateTime.parse("2025-12-31T23:59:59");
        coupon = Coupon.create(
                "testCoupon", "testCode", 20L,
                30, expiredAt, userId
        );

        authenticatedUser = new AuthenticatedUser(userId, "test@gmail.com", "MASTER");
    }

    @Test
    @DisplayName("쿠폰 생성 성공 테스트")
    public void createCouponSuccess() {
        couponId = UUID.randomUUID();
        LocalDateTime expiredAt = LocalDateTime.parse("2025-12-31T23:59:59");
        CreateCouponRequestDto requestDto = new CreateCouponRequestDto(
                "testCoupon", "testCode", 20L,
                30, expiredAt);

        when(couponRepository.findByCode(requestDto.code())).thenReturn(Optional.empty());
        when(meterRegistry.counter(any(String.class), any(String[].class))).thenReturn(counter);
        CouponResponseDto mappedDto = new CouponResponseDto(
                couponId, "testCoupon", "testCode", 20L,
                30, expiredAt
        );
        when(couponMapper.couponToCouponResponseDto(any(Coupon.class))).thenReturn(mappedDto);

        CouponResponseDto responseDto = couponService.createCoupon(requestDto.toDto(), authenticatedUser);

        assertNotNull(responseDto);
    }


    @Test
    @DisplayName("쿠폰 생성 실패 테스트 - 존재하는 코드")
    public void createCouponFail_CouponCodeExists() {
        LocalDateTime expiredAt = LocalDateTime.parse("2025-12-31T23:59:59");
        CreateCouponRequestDto requestDto = new CreateCouponRequestDto(
                "testCoupon", "testCode", 20L,
                30, expiredAt);

        when(couponRepository.findByCode(coupon.getCode())).thenReturn(Optional.of(coupon));
        when(meterRegistry.counter(any(String.class), any(String[].class))).thenReturn(counter);
        CouponException exception = assertThrows(CouponException.class, () ->
                couponService.createCoupon(requestDto.toDto(), authenticatedUser));

        assertEquals(ResponseCode.COUPON_EXISTS, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 단건 조회 성공 테스트")
    public void getCouponSuccess() {
        couponId = UUID.randomUUID();

        when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.of(coupon));
        CouponResponseDto mappedDto = new CouponResponseDto(
                couponId, "testCoupon", "testCode", 20L,
                30, expiredAt
        );
        when(couponMapper.couponToCouponResponseDto(any(Coupon.class))).thenReturn(mappedDto);

        CouponResponseDto responseDto = couponService.getCoupon(couponId);
        assertNotNull(responseDto);
        assertNotNull(mappedDto);
    }
    @Test
    @DisplayName("쿠폰 단건 조회 실패 테스트 - 쿠폰 없음")
    public void getCouponFail_CouponNotFound() {
        couponId = UUID.randomUUID();

        when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.empty());

        CouponException exception = assertThrows(CouponException.class, () ->
                couponService.getCoupon(couponId));

        assertEquals(ResponseCode.COUPON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 전체 조회 성공 테스트")
    public void searchCouponSuccess() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Coupon> couponList = List.of(coupon);
        Page<Coupon> couponPage = new PageImpl<>(couponList, pageable, couponList.size());

        when(couponQueryRepository.findAllByDeletedAtIsNull(null, pageable)).thenReturn(couponPage);

        PageResponseDto<CouponResponseDto> result = couponService.searchCoupon(null, page, size);

        assertNotNull(result);
    }
    @Test
    @DisplayName("쿠폰 전체 조회 실패 테스트 - 쿠폰 없음")
    public void searchCouponFail_CouponNotFound() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Coupon> couponPage = new PageImpl<>(List.of(), pageable, 0);

        when(couponQueryRepository.findAllByDeletedAtIsNull(null, pageable)).thenReturn(couponPage);

        CouponException exception = assertThrows(CouponException.class, () ->
                couponService.searchCoupon(null, page, size));

        assertEquals(ResponseCode.COUPON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 수정 성공 테스트")
    public void updateCouponSuccess() {
        couponId = UUID.randomUUID();

        when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.of(coupon));
        UpdateCouponRequestDto requestDto = new UpdateCouponRequestDto(
                "updateCoupon", "updateCode", 40L,
                350, expiredAt);
        CouponResponseDto mappedDto = new CouponResponseDto(
                couponId, "updateCoupon", "updateCode", 40L,
                350, expiredAt
        );
        when(couponMapper.couponToCouponResponseDto(any(Coupon.class))).thenReturn(mappedDto);
        CouponResponseDto responseDto = couponService.updateCoupon(couponId, authenticatedUser, requestDto.toDto());
        assertNotNull(responseDto);
        assertNotNull(mappedDto);
    }
    @Test
    @DisplayName("쿠폰 수정 실패 테스트 - 쿠폰 없음")
    public void updateCouponFail_CouponNotFound() {
        couponId = UUID.randomUUID();

        when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.empty());
        UpdateCouponRequestDto requestDto = new UpdateCouponRequestDto(
                "updateCoupon", "updateCode", 40L,
                350, expiredAt);
        CouponException exception = assertThrows(CouponException.class, () ->
                couponService.updateCoupon(couponId, authenticatedUser, requestDto.toDto()));

        assertEquals(ResponseCode.COUPON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 삭제 성공 테스트")
    public void deleteCouponSuccess() {
        couponId = UUID.randomUUID();

        when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.of(coupon));

        couponService.deleteCoupon(couponId, authenticatedUser);

        assertNotNull(coupon.getDeletedAt());
    }
    @Test
    @DisplayName("쿠폰 삭제 실패 테스트 - 쿠폰 없음")
    public void deleteCouponFail_CouponNotFound() {
        couponId = UUID.randomUUID();

        when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.empty());

        CouponException exception = assertThrows(CouponException.class, () ->
                couponService.deleteCoupon(couponId, authenticatedUser));

        assertEquals(ResponseCode.COUPON_NOT_FOUND, exception.getErrorCode());
    }
}
