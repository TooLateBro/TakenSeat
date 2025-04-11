package com.taken_seat.review_service.infrastructure.repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.taken_seat.review_service.domain.model.QReview;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.ReviewQuerydslRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewQuerydslRepositoryImpl implements ReviewQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	private static final List<Integer> VALID_SIZES = Arrays.asList(10, 30, 50);
	private static final List<String> VALID_SORT_BY = Arrays.asList("createdAt", "updatedAt", "deletedAt");

	@Override
	public Page<Review> search(String query, String category, int page, int size, String sortBy, String order) {
		size = validateSize(size);
		Sort sort = getSortOrder(sortBy, order);
		Pageable pageable = PageRequest.of(page, size, sort);

		QReview review = QReview.review;

		List<Review> contents = queryFactory
			.selectFrom(review)
			.where(
				review.deletedAt.isNull(),
				buildSearchCondition(query, category)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(toOrderSpecifier(sortBy, order))
			.fetch();

		Long total = queryFactory
			.select(review.count())
			.from(review)
			.where(
				review.deletedAt.isNull(),
				buildSearchCondition(query, category)
			)
			.fetchOne();

		return new PageImpl<>(contents, pageable, total != null ? total : 0);
	}

	private BooleanExpression buildSearchCondition(String query, String category) {
		if (!StringUtils.hasText(query) || !StringUtils.hasText(category))
			return null;

		QReview review = QReview.review;

		return switch (category) {
			case "title" -> titleContains(query);
			case "performanceId" -> performanceIdEq(query);
			case "authorEmail" -> authorEmailContains(query);
			default -> null;
		};
	}

	private BooleanExpression titleContains(String query) {
		return QReview.review.title.containsIgnoreCase(query);
	}

	private BooleanExpression performanceIdEq(String query) {
		try {
			UUID uuid = UUID.fromString(query);
			return QReview.review.performanceId.eq(uuid);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("공연 ID는 UUID 형식이어야 합니다: " + query);
		}
	}

	private BooleanExpression authorEmailContains(String query) {
		return QReview.review.authorEmail.containsIgnoreCase(query);
	}

	private int validateSize(int size) {
		return VALID_SIZES.contains(size) ? size : 10;
	}

	private Sort getSortOrder(String sortBy, String order) {
		if (!VALID_SORT_BY.contains(sortBy)) {
			throw new IllegalArgumentException("정렬 필드는 createdAt, updatedAt, deletedAt 만 허용됩니다.");
		}
		return "desc".equalsIgnoreCase(order) ?
			Sort.by(Sort.Order.desc(sortBy)) :
			Sort.by(Sort.Order.asc(sortBy));
	}

	private OrderSpecifier<?> toOrderSpecifier(String sortBy, String order) {
		PathBuilder<Review> pathBuilder = new PathBuilder<>(Review.class, "review");
		Order direction = "desc".equalsIgnoreCase(order) ? Order.DESC : Order.ASC;
		return switch (sortBy) {
			case "createdAt" ->
				new OrderSpecifier<>(direction, pathBuilder.get("createdAt", java.time.LocalDateTime.class));
			case "updatedAt" ->
				new OrderSpecifier<>(direction, pathBuilder.get("updatedAt", java.time.LocalDateTime.class));
			case "deletedAt" ->
				new OrderSpecifier<>(direction, pathBuilder.get("deletedAt", java.time.LocalDateTime.class));
			default -> throw new IllegalArgumentException("정렬 필드는 createdAt, updatedAt, deletedAt 만 허용됩니다.");
		};
	}
}