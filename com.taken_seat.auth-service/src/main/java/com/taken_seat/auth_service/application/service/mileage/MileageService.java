package com.taken_seat.auth_service.application.service.mileage;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageDto;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.mileage.MileageQueryRepository;
import com.taken_seat.auth_service.domain.repository.mileage.MileageRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MileageService {

    private final UserRepository userRepository;
    private final MileageRepository mileageRepository;
    private final MileageQueryRepository mileageQueryRepository;

    public MileageService(UserRepository userRepository, MileageRepository mileageRepository, MileageQueryRepository mileageQueryRepository) {
        this.userRepository = userRepository;
        this.mileageRepository = mileageRepository;
        this.mileageQueryRepository = mileageQueryRepository;
    }

    @Transactional
    @CachePut(cacheNames = "mileageCache", key = "#result.mileageId")
    public UserMileageResponseDto createMileageUser(UUID userId, UserMileageDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Mileage mileage = Mileage.create(user, dto.getCount());

        mileageRepository.save(mileage);

        return UserMileageResponseDto.of(mileage);
    }

    @Transactional(readOnly = true)
    public UserMileageResponseDto getMileageUser(UUID mileageId) {
        Mileage mileage = mileageRepository.findByIdAndDeletedAtIsNull(mileageId)
                .orElseThrow(()-> new IllegalArgumentException("마일리지를 찾을 수 없습니다."));

        return UserMileageResponseDto.of(mileage);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "searchCache", key = "#userId + '-' + #page + '-' + #size")
    public PageResponseDto<UserMileageResponseDto> getMileageHistoryUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Mileage> mileage = mileageRepository.findByUserIdAndDeletedAtIsNull(userId, pageable);

        Page<UserMileageResponseDto> mileageInfo = mileage.map(UserMileageResponseDto::of);

        return PageResponseDto.of(mileageInfo);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "searchCache", key = "#startCount + '-' +#endCount +'-'+ #page + '-' + #size")
    public PageResponseDto<UserMileageResponseDto> searchMileageUser(
            Integer startCount, Integer endCount, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Mileage> mileageInfo = mileageQueryRepository.findAllByDeletedAtIsNull(startCount, endCount, pageable);

        Page<UserMileageResponseDto> userMileages = mileageInfo.map(UserMileageResponseDto::of);
        return PageResponseDto.of(userMileages);
    }

    @Transactional
    @CachePut(cacheNames = "mileageCache", key = "#result.mileageId")
    @CacheEvict(cacheNames = "searchCache", allEntries = true)
    public UserMileageResponseDto updateMileageUser(UUID mileageId, UUID userId, UserMileageDto dto) {
        Mileage mileage = mileageRepository.findByIdAndDeletedAtIsNull(mileageId)
                .orElseThrow(()-> new IllegalArgumentException("마일리지를 찾을 수 없습니다."));

        mileage.update(dto.getCount(), userId);

        return UserMileageResponseDto.of(mileage);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "mileageCache", allEntries = true),
            @CacheEvict(cacheNames = "searchCache", allEntries = true)
    })
    public void deleteMileageUser(UUID mileageId, UUID userId) {
        Mileage mileage = mileageRepository.findByIdAndDeletedAtIsNull(mileageId)
                .orElseThrow(()-> new IllegalArgumentException("마일리지를 찾을 수 없습니다."));

        mileage.del(userId);
    }
}
