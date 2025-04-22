package com.taken_seat.performance_service.performance.domain.repository.spec;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.domain.model.Performance;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PerformanceSpecification {

	private static final String DELETED_AT = "deletedAt";
	private static final String TITLE = "title";
	private static final String START_AT = "startAt";
	private static final String STATUS = "status";

	private static String wildcard(String keyword) {
		return "%" + keyword + "%";
	}

	public static Specification<Performance> withFilter(SearchFilterParam filterParam) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.isNull(root.get(DELETED_AT)));

			addTitleCondition(predicates, root, criteriaBuilder, filterParam);
			addDateCondition(predicates, root, criteriaBuilder, filterParam);
			addStatusCondition(predicates, root, criteriaBuilder, filterParam);

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	/**
	 * 타이틀 검색 조건
	 */
	private static void addTitleCondition(List<Predicate> predicates, Root<Performance> root,
		CriteriaBuilder cb, SearchFilterParam filterParam) {
		if (filterParam.getTitle() != null && !filterParam.getTitle().isBlank()) {
			predicates.add(cb.like(root.get(TITLE), wildcard(filterParam.getTitle())));
		}
	}

	/**
	 * 날짜 조건
	 * 1. 둘 다 입력: 시작일 ~ 종료일
	 * 2. 시작일만 입력: 시작일 이후 시작되는 공연 조회
	 * 3. 종료일만 입력: 종료일 이전에 시작되는 공연 조회
	 */
	private static void addDateCondition(List<Predicate> predicates, Root<Performance> root,
		CriteriaBuilder cb, SearchFilterParam filterParam) {

		LocalDateTime start = filterParam.getStartAt();
		LocalDateTime end = filterParam.getEndAt();

		if (start != null && end != null) {
			predicates.add(cb.between(root.get(START_AT), start, end));
		} else if (start != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get(START_AT), start));
		} else if (end != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get(START_AT), end));
		}
	}

	/**
	 * 상태 조건
	 */
	private static void addStatusCondition(List<Predicate> predicates, Root<Performance> root,
		CriteriaBuilder cb, SearchFilterParam filterParam) {
		if (filterParam.getStatus() != null) {
			predicates.add(cb.equal(root.get(STATUS), filterParam.getStatus()));
		}
	}
}

