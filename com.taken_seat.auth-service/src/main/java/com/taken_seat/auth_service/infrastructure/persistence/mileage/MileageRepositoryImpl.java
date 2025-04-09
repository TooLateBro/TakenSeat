package com.taken_seat.auth_service.infrastructure.persistence.mileage;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.repository.mileage.MileageRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MileageRepositoryImpl extends JpaRepository<Mileage, UUID>, MileageRepository {
}
