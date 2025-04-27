package com.taken_seat.auth_service.application.dto.user.v1;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.UUID;

@Mapper(componentModel = "spring") // Spring 컨테이너에서 빈으로 등록해줌
public interface UserMapper {

    @Mapping(target = "mileage", ignore = true) // mileage 필드는 별도로 처리
    @Mapping(target = "userCoupons", ignore = true) // userCoupons 필드는 별도로 처리
    UserInfoResponseDtoV1 userToUserInfoResponseDto(User user);

    // 수동 구현이 필요한 매핑은 default 메서드로 작성
    default UserInfoResponseDtoV1 userToUserInfoDetailsResponseDto(User user, Page<UserCoupon> userCoupons) {
        if (user == null) {
            return null;
        }
        PageResponseDto<UUID> couponsPage = PageResponseDto.of(userCoupons.map(UserCoupon::getCouponId));

        Integer latestMileage = user.getMileages().stream()
                .max(Comparator.comparing(Mileage::getUpdatedAt))
                .map(Mileage::getMileage)
                .orElse(0);

        return new UserInfoResponseDtoV1(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                latestMileage,
                couponsPage
        );
    }
}
