package com.taken_seat.auth_service.application.service.mileage;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.mileage.MileageMapper;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageDto;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.mileage.MileageQueryRepository;
import com.taken_seat.auth_service.domain.repository.mileage.MileageRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.customException.MileageException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
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
public class MileageServiceImpl implements MileageService {

    private final MileageMapper mileageMapper;
    private final UserRepository userRepository;
    private final MileageRepository mileageRepository;
    private final MileageQueryRepository mileageQueryRepository;

    public MileageServiceImpl(MileageMapper mileageMapper, UserRepository userRepository,
                              MileageRepository mileageRepository, MileageQueryRepository mileageQueryRepository) {
        this.mileageMapper = mileageMapper;
        this.userRepository = userRepository;
        this.mileageRepository = mileageRepository;
        this.mileageQueryRepository = mileageQueryRepository;
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "searchUser", allEntries = true),
            @CacheEvict(cacheNames = "searchMileage", allEntries = true)
    })
    public UserMileageResponseDto createMileageUser(UUID userId, UserMileageDto dto) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        Mileage mileage = Mileage.create(user, dto.mileage());

        if (mileage.getCreatedBy() != null){
            mileage.preUpdate(userId);
        }

        mileageRepository.save(mileage);

        return mileageMapper.userToUserMileageResponseDto(mileage);
    }

    @Transactional(readOnly = true)
    @Override
    public UserMileageResponseDto getMileageUser(UUID mileageId) {
        Mileage mileage = mileageRepository.findByIdAndDeletedAtIsNull(mileageId)
                .orElseThrow(()-> new AuthException(ResponseCode.MILEAGE_NOT_FOUND));

        return mileageMapper.userToUserMileageResponseDto(mileage);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchMileage", key = "#userId + '-' + #page + '-' + #size")
    public PageResponseDto<UserMileageResponseDto> getMileageHistoryUser(UUID userId, int page, int size) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Mileage> mileage = mileageRepository.findMileageByUserIdAndDeletedAtIsNull(user.getId(), pageable);

        Page<UserMileageResponseDto> mileageInfo = mileage.map(mileageMapper::userToUserMileageResponseDto);

        return PageResponseDto.of(mileageInfo);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchMileage", key = "#startCount + '-' +#endCount +'-'+ #page + '-' + #size")
    public PageResponseDto<UserMileageResponseDto> searchMileageUser(
            Integer startCount, Integer endCount, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Mileage> mileageInfo = mileageQueryRepository.findAllByDeletedAtIsNull(startCount, endCount, pageable);

        if (mileageInfo.isEmpty()) {
            throw new MileageException(ResponseCode.MILEAGE_NOT_FOUND);
        }

        Page<UserMileageResponseDto> userMileages = mileageInfo.map(mileageMapper::userToUserMileageResponseDto);
        return PageResponseDto.of(userMileages);
    }

    @Transactional
    @Override
    @CachePut(cacheNames = "mileageCache", key = "#result.mileageId")
    @CacheEvict(cacheNames = "searchMileage", allEntries = true)
    public UserMileageResponseDto updateMileageUser(UUID mileageId, AuthenticatedUser authenticatedUser, UserMileageDto dto) {
        Mileage mileage = mileageRepository.findByIdAndDeletedAtIsNull(mileageId)
                .orElseThrow(()-> new MileageException(ResponseCode.MILEAGE_NOT_FOUND));

        mileage.update(dto.mileage(), authenticatedUser.getUserId());

        return mileageMapper.userToUserMileageResponseDto(mileage);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "mileageCache", allEntries = true),
            @CacheEvict(cacheNames = "searchMileage", allEntries = true)
    })
    public void deleteMileageUser(UUID mileageId, AuthenticatedUser authenticatedUser) {
        Mileage mileage = mileageRepository.findByIdAndDeletedAtIsNull(mileageId)
                .orElseThrow(()-> new MileageException(ResponseCode.MILEAGE_NOT_FOUND));

        mileage.delete(authenticatedUser.getUserId());
    }
}
