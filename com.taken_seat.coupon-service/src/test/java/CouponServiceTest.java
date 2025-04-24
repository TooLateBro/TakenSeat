import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.service.CouponServiceImpl;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponQueryRepository;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import com.taken_seat.coupon_service.presentation.dto.CreateCouponRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponQueryRepository couponQueryRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon coupon;
    private UUID couponId;
    private AuthenticatedUser authenticatedUser;

    private UUID userId = UUID.randomUUID();
    @BeforeEach
    public void setUp() {
        LocalDateTime expiredAt = LocalDateTime.parse("2025-12-31T23:59:59");
        coupon = Coupon.create(
                "testCoupon", "testCode", 20L,
                30, expiredAt, userId
        );

        authenticatedUser = new AuthenticatedUser(userId, "test@gmail.com", "MASTER");
    }

    @Test
    @DisplayName("쿠폰 생성 성공 테스트")
    public void createCouponSuccess() {
        LocalDateTime expiredAt = LocalDateTime.parse("2025-12-31T23:59:59");
        CreateCouponRequestDto requestDto = new CreateCouponRequestDto(
                "testCoupon", "testCode", 20L,
                30, expiredAt);

        when(couponRepository.findByCode(coupon.getCode())).thenReturn(Optional.empty());
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

        CouponException exception = assertThrows(CouponException.class, () ->
                couponService.createCoupon(requestDto.toDto(), authenticatedUser));

        assertEquals(ResponseCode.COUPON_EXISTS, exception.getErrorCode());
    }

}
