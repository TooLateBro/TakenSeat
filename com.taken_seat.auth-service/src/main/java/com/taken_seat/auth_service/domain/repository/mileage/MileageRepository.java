package com.taken_seat.auth_service.domain.repository.mileage;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import org.springframework.stereotype.Repository;

@Repository
public interface MileageRepository {

    Mileage save(Mileage mileage);
}
