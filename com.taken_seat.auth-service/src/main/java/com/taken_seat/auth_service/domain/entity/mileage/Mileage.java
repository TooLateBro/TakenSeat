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

    @Column(name = "mileage", nullable = false)
    private Integer mileage;

    public static Mileage create(User user, Integer mileage){
        Mileage mileages = Mileage.builder()
                .user(user)
                .mileage(mileage)
                .build();
        mileages.prePersist(user.getId());
        return mileages;
    }

    public void update(Integer mileage, UUID userId) {
        this.mileage = mileage;
        this.preUpdate(userId);
    }

    // ======================================= 테이블 연관 관게 =======================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private User user;

}