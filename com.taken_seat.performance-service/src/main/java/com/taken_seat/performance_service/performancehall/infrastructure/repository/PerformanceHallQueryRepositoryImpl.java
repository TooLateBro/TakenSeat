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
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallSearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallSearchResponseDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceHallQueryRepositoryImpl implements PerformanceHallQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private static final QPerformanceHall performanceHall = QPerformanceHall.performanceHall;
	private static final String PERFORMANCE_HALL = "performanceHall";

	@Override
	public Page<HallSearchResponseDto> searchByFilter(HallSearchFilterParam hallSearchFilterParam, Pageable pageable) {

		BooleanBuilder builder = buildConditions(hallSearchFilterParam);

		List<HallSearchResponseDto> content = fetchContent(pageable, builder);
		long total = fetchTotal(builder);

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanBuilder buildConditions(HallSearchFilterParam hallSearchFilterParam) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(performanceHall.deletedAt.isNull());

		if (hallSearchFilterParam.name() != null && !hallSearchFilterParam.name().isBlank()) {
			builder.and(performanceHall.name.containsIgnoreCase(hallSearchFilterParam.name()));
		}

		if (hallSearchFilterParam.address() != null && !hallSearchFilterParam.address().isBlank()) {
			builder.and(performanceHall.address.containsIgnoreCase(hallSearchFilterParam.address()));
		}

		Integer min = hallSearchFilterParam.minSeats();
		Integer max = hallSearchFilterParam.maxSeats();

		if (min != null && max != null) {
			builder.and(performanceHall.totalSeats.between(min, max));
		} else if (min != null) {
			builder.and(performanceHall.totalSeats.goe(min));
		} else if (max != null) {
			builder.and(performanceHall.totalSeats.loe(max));
		}

		return builder;
	}

	private List<HallSearchResponseDto> fetchContent(
		Pageable pageable,
		BooleanBuilder builder
	) {
		return jpaQueryFactory
			.select(Projections.constructor(
				HallSearchResponseDto.class,
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
