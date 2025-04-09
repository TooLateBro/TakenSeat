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

	public static Specification<Performance> withFilter(SearchFilterParam filterParam) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

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
		if (filterParam.getTitle() != null) {
			predicates.add(cb.like(root.get("title"), "%" + filterParam.getTitle() + "%"));
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
			predicates.add(cb.between(root.get("startAt"), start, end));
		} else if (start != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("startAt"), start));
		} else if (end != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("startAt"), end));
		}
	}

	/**
	 * 상태 조건
	 */
	private static void addStatusCondition(List<Predicate> predicates, Root<Performance> root,
		CriteriaBuilder cb, SearchFilterParam filterParam) {
		if (filterParam.getStatus() != null) {
			predicates.add(cb.equal(root.get("status"), filterParam.getStatus()));
		}
	}
}

