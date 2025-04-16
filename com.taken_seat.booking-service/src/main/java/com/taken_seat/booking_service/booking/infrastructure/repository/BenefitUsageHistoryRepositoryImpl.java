package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.BenefitUsageHistory;
import com.taken_seat.booking_service.booking.domain.repository.BenefitUsageHistoryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BenefitUsageHistoryRepositoryImpl implements BenefitUsageHistoryRepository {

	private final BenefitUsageHistoryJpaRepository benefitUsageHistoryJpaRepository;

	@Override
	public BenefitUsageHistory save(BenefitUsageHistory benefitUsageHistory) {
		return benefitUsageHistoryJpaRepository.save(benefitUsageHistory);
	}

	@Override
	public Optional<BenefitUsageHistory> findByBookingIdAndRefundedIsFalse(UUID bookingId) {
		return benefitUsageHistoryJpaRepository.findByBookingIdAndRefundedIsFalse(bookingId);
	}
}