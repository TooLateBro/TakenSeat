package com.taken_seat.auth_service.domain.repository.mileage;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MileageQueryRepository {

    Page<Mileage> findAllByDeletedAtIsNull(Integer startCount , Integer endCount, Pageable pageable);
}
