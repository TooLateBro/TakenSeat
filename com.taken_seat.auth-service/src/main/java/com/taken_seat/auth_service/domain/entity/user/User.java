package com.taken_seat.auth_service.domain.entity.user;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.vo.Role;
import com.taken_seat.common_service.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

// ======================================= 테이블 연관 관게 =======================================

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserCoupon> userCoupons = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Mileage> mileages = new ArrayList<>();

    public static User create(String username, String email, String phone, String password, Role role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .password(password)
                .role(role)
                .build();

        user.prePersist(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        return user;
    }

    public void update(String username, String email, String phone, String password, Role role) {
        if (username != null) this.username = username;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
        if (password != null) this.password = password;
        if (role != null) this.role = role;
    }
}
