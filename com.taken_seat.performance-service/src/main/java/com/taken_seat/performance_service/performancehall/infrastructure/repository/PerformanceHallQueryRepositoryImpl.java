package com.taken_seat.performance_service.performancehall.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.taken_seat.performance_service.common.support.QueryDslOrderUtil;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.QPerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallQueryRepository;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SearchResponseDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceHallQueryRepositoryImpl implements PerformanceHallQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private static final QPerformanceHall performanceHall = QPerformanceHall.performanceHall;
	private static final String PERFORMANCE_HALL = "performanceHall";

	@Override
	public Page<SearchResponseDto> searchByFilter(SearchFilterParam searchFilterParam, Pageable pageable) {

		BooleanBuilder builder = buildConditions(searchFilterParam);

		List<SearchResponseDto> content = fetchContent(pageable, builder);
		long total = fetchTotal(builder);

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanBuilder buildConditions(SearchFilterParam searchFilterParam) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(performanceHall.deletedAt.isNull());

		if (searchFilterParam.name() != null && !searchFilterParam.name().isBlank()) {
			builder.and(performanceHall.name.containsIgnoreCase(searchFilterParam.name()));
		}

		if (searchFilterParam.address() != null && !searchFilterParam.address().isBlank()) {
			builder.and(performanceHall.address.containsIgnoreCase(searchFilterParam.address()));
		}

		Integer min = searchFilterParam.minSeats();
		Integer max = searchFilterParam.maxSeats();

		if (min != null && max != null) {
			builder.and(performanceHall.totalSeats.between(min, max));
		} else if (min != null) {
			builder.and(performanceHall.totalSeats.goe(min));
		} else if (max != null) {
			builder.and(performanceHall.totalSeats.loe(max));
		}

		return builder;
	}

	private List<SearchResponseDto> fetchContent(
		Pageable pageable,
		BooleanBuilder builder
	) {
		return jpaQueryFactory
			.select(Projections.constructor(
				SearchResponseDto.class,
				performanceHall.id,
				performanceHall.name,
				performanceHall.totalSeats
			))
			.from(performanceHall)
			.where(builder)
			.orderBy(QueryDslOrderUtil.toOrderSpecifiers(pageable.getSort(), PerformanceHall.class, PERFORMANCE_HALL))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private long fetchTotal(BooleanBuilder builder) {
		Long count = jpaQueryFactory
			.select(performanceHall.count())
			.from(performanceHall)
			.where(builder)
			.fetchOne();

		return count != null ? count : 0;
	}
}
