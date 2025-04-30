package com.taken_seat.performance_service.performance.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.taken_seat.performance_service.common.support.QueryDslOrderUtil;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.QPerformance;
import com.taken_seat.performance_service.performance.domain.model.QPerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceQueryRepository;
import com.taken_seat.performance_service.performance.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.presentation.dto.response.SearchResponseDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceQueryRepositoryImpl implements PerformanceQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private static final String PERFORMANCE = "performance";
	private static final QPerformance performance = QPerformance.performance;

	@Override
	public Page<SearchResponseDto> searchByFilter(SearchFilterParam searchFilterParam, Pageable pageable) {

		BooleanBuilder builder = buildConditions(searchFilterParam);

		List<SearchResponseDto> content = fetchContent(pageable, builder);
		long total = fetchTotal(builder);

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public List<UUID> findAllPerformanceScheduleIds() {

		QPerformance performance = QPerformance.performance;
		QPerformanceSchedule performanceSchedule = QPerformanceSchedule.performanceSchedule;

		return jpaQueryFactory
			.select(performanceSchedule.id)
			.from(performanceSchedule)
			.join(performanceSchedule.performance, performance)
			.where(
				performanceSchedule.deletedAt.isNull(),
				performance.deletedAt.isNull())
			.fetch();
	}

	private BooleanBuilder buildConditions(SearchFilterParam searchFilterParam) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(performance.deletedAt.isNull());

		if (searchFilterParam.title() != null && !searchFilterParam.title().isBlank()) {
			builder.and(performance.title.containsIgnoreCase(searchFilterParam.title()));
		}

		if (searchFilterParam.startAt() != null && searchFilterParam.endAt() != null) {
			builder.and(performance.startAt.between(searchFilterParam.startAt(), searchFilterParam.endAt()));
		} else if (searchFilterParam.startAt() != null) {
			builder.and(performance.startAt.goe(searchFilterParam.startAt()));
		} else if (searchFilterParam.endAt() != null) {
			builder.and(performance.startAt.loe(searchFilterParam.endAt()));
		}

		if (searchFilterParam.status() != null) {
			builder.and(performance.status.eq(searchFilterParam.status()));
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
				performance.id,
				performance.title,
				performance.startAt,
				performance.endAt,
				performance.status,
				performance.posterUrl
			))
			.from(performance)
			.where(builder)
			.orderBy(QueryDslOrderUtil.toOrderSpecifiers(pageable.getSort(), Performance.class, PERFORMANCE))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private long fetchTotal(BooleanBuilder builder) {
		Long count = jpaQueryFactory
			.select(performance.count())
			.from(performance)
			.where(builder)
			.fetchOne();

		return count != null ? count : 0;
	}
}
