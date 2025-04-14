package com.taken_seat.auth_service.domain.repository.mileage;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MileageRepository {

    Mileage save(Mileage mileage);

    Page<Mileage> findByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable);

    Optional<Mileage> findByIdAndDeletedAtIsNull(UUID mileageId);

    Optional<Mileage> findTopByUserIdOrderByUpdatedAtDesc(UUID userId);
}
