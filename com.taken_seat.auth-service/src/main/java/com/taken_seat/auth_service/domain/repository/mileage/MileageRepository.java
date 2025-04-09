package com.taken_seat.auth_service.domain.repository.mileage;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MileageRepository {

    Mileage save(Mileage mileage);

    Optional<Mileage> findById(UUID mileageId);
}
