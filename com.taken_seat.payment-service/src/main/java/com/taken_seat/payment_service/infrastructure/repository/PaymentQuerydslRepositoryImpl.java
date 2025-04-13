package com.taken_seat.payment_service.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.QPayment;
import com.taken_seat.payment_service.domain.repository.PaymentQuerydslRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentQuerydslRepositoryImpl implements PaymentQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	private static final List<Integer> VALID_SIZES = Arrays.asList(10, 30, 50);
	private static final List<String> VALID_SORT_BY = Arrays.asList("createdAt", "updatedAt", "deletedAt");

	@Override
	public Page<Payment> search(String query, String category, int page, int size, String sortBy, String order) {
		size = validateSize(size);
		Sort sort = getSortOrder(sortBy, order);
		Pageable pageable = PageRequest.of(page, size, sort);

		QPayment payment = QPayment.payment;

		List<Payment> contents = queryFactory
			.selectFrom(payment)
			.where(
				payment.deletedAt.isNull(),
				buildSearchCondition(query, category)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(toOrderSpecifier(sortBy, order))
			.fetch();

		Long total = queryFactory
			.select(payment.count())
			.from(payment)
			.where(
				payment.deletedAt.isNull(),
				buildSearchCondition(query, category)
			)
			.fetchOne();

		return new PageImpl<>(contents, pageable, total != null ? total : 0);
	}

	private BooleanExpression buildSearchCondition(String query, String category) {
		if (!StringUtils.hasText(query) || !StringUtils.hasText(category))
			return null;

		return switch (category) {
			case "status" -> statusContains(query);
			case "approvedAt" -> approvedAtEquals(query);
			case "refundRequestedAt" -> refundRequestedAtEquals(query);
			default -> null;
		};
	}

	private BooleanExpression statusContains(String query) {
		try {
			PaymentStatus status = PaymentStatus.valueOf(query.toUpperCase());

			return QPayment.payment.paymentStatus.eq(status);

		} catch (IllegalArgumentException e) {
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT
				, "올바른 결제 상태를 입력해주세요. (예: COMPLETED, FAILED, REFUNDED,DELETED)");
		}
	}

	private BooleanExpression approvedAtEquals(String query) {
		try {
			LocalDate targetDate = LocalDate.parse(query, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			LocalDateTime startOfDay = targetDate.atStartOfDay();
			LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX); // 23:59:59.999999999

			return QPayment.payment.approvedAt.between(startOfDay, endOfDay);
		} catch (DateTimeParseException e) {
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT,
				"올바른 날짜 형식이 아닙니다. (예: 2025-04-13)");
		}
	}

	private BooleanExpression refundRequestedAtEquals(String query) {
		try {
			LocalDate targetDate = LocalDate.parse(query, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			LocalDateTime startOfDay = targetDate.atStartOfDay();
			LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

			return QPayment.payment.refundRequestedAt.between(startOfDay, endOfDay);
		} catch (DateTimeParseException e) {
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT,
				"올바른 날짜 형식이 아닙니다. (예: 2025-04-13)");
		}
	}

	private int validateSize(int size) {
		return VALID_SIZES.contains(size) ? size : 10;
	}

	private Sort getSortOrder(String sortBy, String order) {
		if (!VALID_SORT_BY.contains(sortBy)) {
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT,
				"정렬 필드는 createdAt, updatedAt, deletedAt 만 허용됩니다.");
		}
		return "desc".equalsIgnoreCase(order) ?
			Sort.by(Sort.Order.desc(sortBy)) :
			Sort.by(Sort.Order.asc(sortBy));
	}

	private OrderSpecifier<?> toOrderSpecifier(String sortBy, String order) {
		PathBuilder<Payment> pathBuilder = new PathBuilder<>(Payment.class, "payment");
		Order direction = "desc".equalsIgnoreCase(order) ? Order.DESC : Order.ASC;
		return switch (sortBy) {
			case "createdAt" ->
				new OrderSpecifier<>(direction, pathBuilder.get("createdAt", java.time.LocalDateTime.class));
			case "updatedAt" ->
				new OrderSpecifier<>(direction, pathBuilder.get("updatedAt", java.time.LocalDateTime.class));
			case "deletedAt" ->
				new OrderSpecifier<>(direction, pathBuilder.get("deletedAt", java.time.LocalDateTime.class));
			default -> throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT,
				"정렬 필드는 createdAt, updatedAt, deletedAt 만 허용됩니다.");
		};
	}
}
