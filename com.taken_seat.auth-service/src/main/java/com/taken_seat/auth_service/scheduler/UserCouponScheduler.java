package com.taken_seat.auth_service.scheduler;

import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@Component
public class UserCouponScheduler {

    private final UserCouponRepository userCouponRepository;
    private final CacheManager cacheManager;

    public UserCouponScheduler(UserCouponRepository userCouponRepository, CacheManager cacheManager) {
        this.userCouponRepository = userCouponRepository;
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron ="0 */5 * * * *")
    @Transactional
    public void changeIsActiveFromExpiredCoupon() {
        List<UserCoupon> userCoupons = userCouponRepository.findAllByExpiredAtBeforeAndIsActiveTrue(
                LocalDateTime.now());

        Cache cache = cacheManager.getCache("searchUser");
        if (cache != null) {
            cache.clear();
        }

        userCoupons.forEach(userCoupon -> {
            userCoupon.updateActive(false, userCoupon.getUser().getId());
        });
    }
}
