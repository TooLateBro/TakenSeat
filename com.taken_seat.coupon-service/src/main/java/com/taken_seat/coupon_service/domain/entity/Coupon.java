package com.taken_seat.coupon_service.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "discount", nullable = false)
    private Integer discount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private UUID createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Column
    private UUID updatedBy;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private UUID deletedBy;

    @PrePersist
    protected void onCreate() {
        LocalDateTime time = LocalDateTime.now();
        this.createdAt = time;
        this.updatedAt = time;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Coupon create(String name, String code, Integer quantity, Integer discount, LocalDateTime expiredAt) {
        return Coupon.builder()
                .name(name)
                .code(code)
                .quantity(quantity)
                .discount(discount)
                .isActive(true)
                .expiredAt(expiredAt)
                .build();
    }

    public void del(UUID deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
