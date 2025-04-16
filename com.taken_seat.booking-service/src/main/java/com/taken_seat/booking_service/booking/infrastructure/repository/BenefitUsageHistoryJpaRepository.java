package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.booking_service.booking.domain.BenefitUsageHistory;

public interface BenefitUsageHistoryJpaRepository extends JpaRepository<BenefitUsageHistory, UUID> {
	Optional<BenefitUsageHistory> findByBookingIdAndRefundedIsFalse(UUID bookingId);
}