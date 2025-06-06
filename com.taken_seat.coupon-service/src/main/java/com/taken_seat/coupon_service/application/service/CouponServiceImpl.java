package com.taken_seat.coupon_service.application.service;

import java.util.UUID;

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

import com.taken_seat.common_service.aop.TrackLatency;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.coupon_service.application.dto.CouponDto;
import com.taken_seat.coupon_service.application.dto.CouponMapper;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponQueryRepository;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;

import io.micrometer.core.instrument.MeterRegistry;

@Service
public class CouponServiceImpl implements CouponService {

	private final CouponMapper couponMapper;
	private final CouponRepository couponRepository;
	private final CouponQueryRepository couponQueryRepository;
	private final MeterRegistry meterRegistry;

	public CouponServiceImpl(CouponMapper couponMapper, CouponRepository couponRepository,
		CouponQueryRepository couponQueryRepository, MeterRegistry meterRegistry) {
		this.couponMapper = couponMapper;
		this.couponRepository = couponRepository;
		this.couponQueryRepository = couponQueryRepository;
		this.meterRegistry = meterRegistry;
	}

	@TrackLatency(
		value = "coupon_create_seconds",
		description = "쿠폰 생성 처리 시간(초)"
	)
	@Transactional
	@Override
	@CachePut(cacheNames = "couponCache", key = "#result.id")
	public CouponResponseDto createCoupon(CouponDto dto, AuthenticatedUser authenticatedUser) {
		try {
			if (couponRepository.findByCode(dto.code()).isPresent()) {
				meterRegistry.counter("coupon_create_total", "result", "fail").increment();
				throw new CouponException(ResponseCode.COUPON_EXISTS);
			}

			Coupon coupon = Coupon.create(
				dto.name(), dto.code(), dto.quantity(),
				dto.discount(), dto.expiredAt(), authenticatedUser.getUserId()
			);
			couponRepository.save(coupon);

			meterRegistry.counter("coupon_create_total", "result", "success").increment();

			return couponMapper.couponToCouponResponseDto(coupon);
		} catch (Exception e) {
			meterRegistry.counter("coupon_create_total", "result", "fail").increment();
			throw e;
		}
	}

	@TrackLatency(
		value = "coupon_get_seconds",
		description = "쿠폰 상세 조회 처리 시간(초)"
	)
	@Transactional(readOnly = true)
	@Override
	public CouponResponseDto getCoupon(UUID couponId) {
		Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
			.orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));

		return couponMapper.couponToCouponResponseDto(coupon);
	}

	@TrackLatency(
		value = "coupon_search_seconds",
		description = "쿠폰 목록 검색 처리 시간(초)"
	)
	@Transactional(readOnly = true)
	@Override
	@Cacheable(cacheNames = "searchCache", key = "#name +'-'+ #page + '-' + #size")
	public PageResponseDto<CouponResponseDto> searchCoupon(String name, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Coupon> coupons = couponQueryRepository.findAllByDeletedAtIsNull(name, pageable);

		if (coupons.isEmpty()) {
			throw new CouponException(ResponseCode.COUPON_NOT_FOUND);
		}

		Page<CouponResponseDto> couponsInfo = coupons.map(couponMapper::couponToCouponResponseDto);

		return PageResponseDto.of(couponsInfo);
	}

	@Transactional
	@Override
	@CachePut(cacheNames = "couponCache", key = "#result.id")
	@Caching(evict = {
		@CacheEvict(cacheNames = "searchCache", allEntries = true)
	})
	public CouponResponseDto updateCoupon(UUID couponId, AuthenticatedUser authenticatedUser, CouponDto dto) {
		Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
			.orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));

		coupon.update(
			dto.name() != null ? dto.name() : coupon.getName(),
			dto.code() != null ? dto.code() : coupon.getCode(),
			dto.quantity() != null ? dto.quantity() : coupon.getQuantity(),
			dto.discount() != null ? dto.discount() : coupon.getDiscount(),
			dto.expiredAt() != null ? dto.expiredAt() : coupon.getExpiredAt(),
			authenticatedUser.getUserId()
		);
		return couponMapper.couponToCouponResponseDto(coupon);
	}

	@Transactional
	@Override
	@Caching(evict = {
		@CacheEvict(cacheNames = "couponCache", allEntries = true),
		@CacheEvict(cacheNames = "searchCache", allEntries = true)
	})
	public void deleteCoupon(UUID couponId, AuthenticatedUser authenticatedUser) {
		Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
			.orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));

		coupon.delete(authenticatedUser.getUserId());
	}
}