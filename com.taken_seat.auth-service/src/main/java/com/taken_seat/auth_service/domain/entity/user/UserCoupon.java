package com.taken_seat.auth_service.domain.entity.user;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    public static UserCoupon create(User user, UUID couponId) {
        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .couponId(couponId)
                .isActive(true)
                .build();
        userCoupon.prePersist(user.getId());
        return userCoupon;
    }
}