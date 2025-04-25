package com.taken_seat.auth_service.application.dto.user.v2;

import com.taken_seat.auth_service.domain.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Spring 컨테이너에서 빈으로 등록해줌
public interface UserMapper {

    @Mapping(target = "mileage", ignore = true) // mileage 필드는 별도로 처리
    @Mapping(target = "userCoupons", ignore = true) // userCoupons 필드는 별도로 처리
    UserInfoResponseDtoV2 userToUserInfoResponseDto(User user);
}
