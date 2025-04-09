package com.taken_seat.auth_service.domain.entity.mileage;

import com.taken_seat.auth_service.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_mileage")
public class Mileage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "count", nullable = false)
    private Integer count = 0;

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

    public static Mileage create(User user, Integer count){
        return Mileage.builder()
                .user(user)
                .count(count)
                .build();
    }

    // ======================================= 테이블 연관 관게 =======================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private User user;
}