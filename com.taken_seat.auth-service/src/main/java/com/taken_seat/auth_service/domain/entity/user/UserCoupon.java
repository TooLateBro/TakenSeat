package com.taken_seat.auth_service.domain.entity.user;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_user_coupon")
public class UserCoupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private User user;

    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "discount", nullable = false)
    private Integer discount;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public static UserCoupon create(User user, KafkaUserInfoMessage message) {
        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .couponId(message.getCouponId())
                .discount(message.getDiscount())
                .expiredAt(message.getExpiredAt())
                .isActive(true)
                .build();
        userCoupon.prePersist(user.getId());
        return userCoupon;
    }
    public void updateActive(boolean isActive, UUID userId) {
        this.isActive = isActive;
        this.preUpdate(userId);
    }
}