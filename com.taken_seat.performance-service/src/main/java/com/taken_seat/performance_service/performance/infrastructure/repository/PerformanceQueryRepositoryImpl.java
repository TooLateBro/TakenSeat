package com.taken_seat.performance_service.performance.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.taken_seat.performance_service.common.support.QueryDslOrderUtil;
import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.application.dto.response.SearchResponseDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.QPerformance;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceQueryRepository;

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

	private BooleanBuilder buildConditions(SearchFilterParam searchFilterParam) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(performance.deletedAt.isNull());

		if (searchFilterParam.getTitle() != null && !searchFilterParam.getTitle().isBlank()) {
			builder.and(performance.title.containsIgnoreCase(searchFilterParam.getTitle()));
		}

		if (searchFilterParam.getStartAt() != null && searchFilterParam.getEndAt() != null) {
			builder.and(performance.startAt.between(searchFilterParam.getStartAt(), searchFilterParam.getEndAt()));
		} else if (searchFilterParam.getStartAt() != null) {
			builder.and(performance.startAt.goe(searchFilterParam.getStartAt()));
		} else if (searchFilterParam.getEndAt() != null) {
			builder.and(performance.startAt.loe(searchFilterParam.getEndAt()));
		}

		if (searchFilterParam.getStatus() != null) {
			builder.and(performance.status.eq(searchFilterParam.getStatus()));
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
