package com.taken_seat.booking_service.booking.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.taken_seat.booking_service.booking.domain.BenefitUsageHistory;

public interface BenefitUsageHistoryRepository {
	BenefitUsageHistory save(BenefitUsageHistory benefitUsageHistory);

	Optional<BenefitUsageHistory> findByBookingIdAndRefundedIsFalse(UUID bookingId);
}