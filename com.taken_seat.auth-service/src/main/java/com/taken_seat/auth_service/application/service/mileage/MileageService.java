package com.taken_seat.auth_service.application.service.mileage;

import com.taken_seat.auth_service.application.dto.mileage.UserMileageDto;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.mileage.MileageRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MileageService {

    private final UserRepository userRepository;
    private final MileageRepository mileageRepository;

    public MileageService(UserRepository userRepository, MileageRepository mileageRepository) {
        this.userRepository = userRepository;
        this.mileageRepository = mileageRepository;
    }

    @Transactional
    public UserMileageResponseDto createMileageUser(UUID userId, UserMileageDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Mileage mileage = Mileage.create(user, dto.getCount());

        mileageRepository.save(mileage);

        return UserMileageResponseDto.of(mileage);
    }
}
