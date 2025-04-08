package com.taken_seat.payment_service.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.BooleanBuilder;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.QPayment;
import com.taken_seat.payment_service.domain.repository.PaymentQuerydslRepository;

public interface PaymentQuerydslRepositoryImpl extends JpaRepository<Payment, UUID>, QuerydslPredicateExecutor<Payment>,
	PaymentQuerydslRepository {

	// 허용된 페이지 크기 리스트
	List<Integer> VALID_SIZES = Arrays.asList(10, 30, 50);

	// 정렬 가능한 필드 리스트
	List<String> VALID_SORT_BY = Arrays.asList("createdAt", "updatedAt", "deletedAt");
	
	default Page<Payment> findAll(String query, String category, int page, int size, String sortBy,
		String order) {

		size = validateSize(size);

		QPayment payment = QPayment.payment;

		// 검색 조건 생성
		BooleanBuilder builder = buildSearchConditions(query, category, payment);

		Sort sort = getSortOrder(sortBy, order);

		PageRequest pageRequest = PageRequest.of(page, size, sort);

		return findAll(builder, pageRequest);

	}

	private BooleanBuilder buildSearchConditions(String query, String category, QPayment qPayment) {

		BooleanBuilder builder = new BooleanBuilder();

		builder.and(qPayment.deletedAt.isNull());

		if (query == null || query.isEmpty()) {
			return builder;
		}

		if (category == null || category.isEmpty()) {
			return builder;
		} else {
			switch (category) {
				case "status":
					// query 값이 ENUM과 정확히 일치할 때만 필터링
					try {
						PaymentStatus status = PaymentStatus.valueOf(query.toUpperCase());
						builder.and(qPayment.paymentStatus.eq(status));
					} catch (IllegalArgumentException e) {
						// 잘못된 값이면 무시
					}
					break;
				case "approvedAt":
					// yyyy-MM-dd 형태로 입력 시 해당 날짜 전체를 포함
					try {
						LocalDateTime start = LocalDateTime.parse(query + "T00:00:00");
						LocalDateTime end = LocalDateTime.parse(query + "T23:59:59.999999999");
						builder.and(qPayment.approvedAt.between(start, end));
					} catch (Exception e) {
						// 날짜 형식이 잘못되면 무시
					}
					break;
				case "refundRequestedAt":
					try {
						LocalDateTime start = LocalDateTime.parse(query + "T00:00:00");
						LocalDateTime end = LocalDateTime.parse(query + "T23:59:59.999999999");
						builder.and(qPayment.refundRequestedAt.between(start, end));
					} catch (Exception e) {
						// 날짜 형식이 잘못되면 무시
					}
					break;
				default:
					return builder;
			}
		}

		return builder;
	}

	private int validateSize(int size) {
		return VALID_SIZES.contains(size) ? size : 10;
	}

	private Sort getSortOrder(String sortBy, String order) {

		if (!isValidSortBy(sortBy)) {
			throw new IllegalArgumentException("SortBy 는 'createdAt', 'updatedAt', 'deletedAt' 값만 허용합니다.");
		}

		Sort sort = Sort.by(Sort.Order.by(sortBy));

		sort = getSortDirection(sort, order);

		return sort;
	}

	private boolean isValidSortBy(String sortBy) {
		return VALID_SORT_BY.contains(sortBy);
	}

	private Sort getSortDirection(Sort sort, String order) {
		return "desc".equals(order) ? sort.descending() : sort.ascending();
	}
}
