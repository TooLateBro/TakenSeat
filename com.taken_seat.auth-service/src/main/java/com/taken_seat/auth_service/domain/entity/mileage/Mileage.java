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
    private Integer count = 0;

    public static Mileage create(User user, Integer count){
        return Mileage.builder()
                .user(user)
                .count(count)
                .build();
    }

    public void update(Integer count) {
        this.count = count;
    }

    // ======================================= 테이블 연관 관게 =======================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private User user;

}