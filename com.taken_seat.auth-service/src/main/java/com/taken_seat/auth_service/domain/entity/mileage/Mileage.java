package com.taken_seat.auth_service.domain.entity.mileage;

import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.common_service.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_mileage")
public class Mileage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "count", nullable = false)
    private Integer count;

    public static Mileage create(User user, Integer count){
        Mileage mileage = Mileage.builder()
                .user(user)
                .count(count)
                .build();
        mileage.prePersist(user.getId());
        return mileage;
    }

    public void update(Integer count, UUID userId) {
        this.count = count;
        this.preUpdate(userId);
    }

    // ======================================= 테이블 연관 관게 =======================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private User user;

}