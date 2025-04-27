package com.taken_seat.payment_service.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.payment_service.domain.model.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {

	Optional<Payment> findByIdAndDeletedAtIsNull(UUID id);
}
