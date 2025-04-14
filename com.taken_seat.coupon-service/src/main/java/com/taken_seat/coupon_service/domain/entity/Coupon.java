package com.taken_seat.coupon_service.domain.entity;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_coupon")
public class Coupon extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "discount", nullable = false)
    private Integer discount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public static Coupon create(String name, String code, Long quantity, Integer discount, LocalDateTime expiredAt, UUID userId) {
        Coupon coupon = Coupon.builder()
                .name(name)
                .code(code)
                .quantity(quantity)
                .discount(discount)
                .isActive(true)
                .expiredAt(expiredAt)
                .build();
        coupon.prePersist(userId);
        return coupon;
    }
    public void update(String name, String code, Long quantity, Integer discount, LocalDateTime expiredAt, UUID userId) {
        this.name = name;
        this.code = code;
        this.quantity = quantity;
        this.discount = discount;
        this.expiredAt = expiredAt;
        this.preUpdate(userId);
    }

    public void updateQuantity(Long quantity, UUID userId) {
        this.quantity = quantity;
        this.preUpdate(userId);
    }
}
