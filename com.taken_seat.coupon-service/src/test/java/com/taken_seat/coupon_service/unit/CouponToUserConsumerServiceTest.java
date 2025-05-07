package com.taken_seat.coupon_service.unit;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import com.taken_seat.coupon_service.infrastructure.config.redis.RedisOperationService;
import com.taken_seat.coupon_service.infrastructure.kafka.CouponToUserConsumerServiceImpl;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponToUserConsumerServiceTest {

    @InjectMocks
    private CouponToUserConsumerServiceImpl consumerService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private RedisOperationService redisOperationService;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    private static final int TOTAL_REQUESTS = 10000;
    private static final int COUPON_QUANTITY = 1000;
    private static final UUID COUPON_ID = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");

    @BeforeEach
    void setUp() throws InterruptedException {
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
        doNothing().when(rLock).unlock();
    }

    @Test
    @DisplayName("동시에 10000명이 1000개 쿠폰 발급받는 시나리오 테스트 개선버전")
    void testConcurrentCouponIssue() throws InterruptedException {
        Coupon coupon = mock(Coupon.class);

        AtomicLong atomicQuantity = new AtomicLong(COUPON_QUANTITY);
        AtomicInteger issuedCount = new AtomicInteger(0);
        Set<UUID> issuedUsers = ConcurrentHashMap.newKeySet();
        AtomicInteger scriptCallCount = new AtomicInteger(0);

        when(couponRepository.findByIdAndDeletedAtIsNull(eq(COUPON_ID))).thenReturn(Optional.of(coupon));
        when(redisOperationService.hasKey(anyString())).thenReturn(true);
        when(meterRegistry.counter(any(String.class), any(String[].class))).thenReturn(counter);

        when(redisOperationService.getCurrentQuantity(anyString())).thenAnswer(invocation ->
                Math.max(0, atomicQuantity.get())
        );

        when(redisOperationService.evalScript(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
            long current = atomicQuantity.get();
            String userId = invocation.getArgument(2);
            if (current <= 0) {
                return -1L;
            }
            if (issuedUsers.contains(UUID.fromString(userId))) {
                return -2L;
            }
            scriptCallCount.incrementAndGet();
            long updated = atomicQuantity.decrementAndGet();
            issuedUsers.add(UUID.fromString(userId));
            issuedCount.incrementAndGet();
            return updated;
        });
        // when
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final UUID userId = UUID.randomUUID();
            executorService.submit(() -> {
                try {
                    KafkaUserInfoMessage message = createMessage(userId);
                    try {
                        KafkaUserInfoMessage result = consumerService.producerMessage(message);
                        if (result.getStatus() == KafkaUserInfoMessage.Status.SUCCEEDED) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("쿠폰 발급 성공 : " + successCount.get());
        System.out.println("쿠폰 발급 실패 : " + failCount.get());
        System.out.println("쿠폰 발급에 성공한 사용자 : " + issuedCount.get());

       }
    private KafkaUserInfoMessage createMessage(UUID userId) {
        return new KafkaUserInfoMessage(userId, COUPON_ID, null, null, KafkaUserInfoMessage.Status.PENDING);
    }
}