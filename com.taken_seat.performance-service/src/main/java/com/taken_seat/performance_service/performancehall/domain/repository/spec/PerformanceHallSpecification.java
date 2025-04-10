package com.taken_seat.performance_service.performancehall.domain.repository.spec;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.taken_seat.performance_service.performancehall.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PerformanceHallSpecification {

	public static Specification<PerformanceHall> withFilter(SearchFilterParam filterParam) {
		return ((root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

			addNameCondition(predicates, root, criteriaBuilder, filterParam);
			addAddressCondition(predicates, root, criteriaBuilder, filterParam);
			addTotalSeatsCondition(predicates, root, criteriaBuilder, filterParam);

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		});
	}

	/**
	 * 이름 검색
	 */
	private static void addNameCondition(List<Predicate> predicates, Root<PerformanceHall> root, CriteriaBuilder cb,
		SearchFilterParam filterParam) {

		if (filterParam.getName() != null && !filterParam.getName().isBlank()) {
			predicates.add(cb.like(root.get("name"), "%" + filterParam.getName() + "%"));
		}
	}

	/**
	 * 주소 검색
	 */
	private static void addAddressCondition(List<Predicate> predicates, Root<PerformanceHall> root, CriteriaBuilder cb,
		SearchFilterParam filterParam) {

		if (filterParam.getAddress() != null && !filterParam.getAddress().isBlank()) {
			predicates.add(cb.like(root.get("address"), "%" + filterParam.getAddress() + "%"));
		}
	}

	/**
	 * 좌석 수 필터링
	 */
	private static void addTotalSeatsCondition(List<Predicate> predicates, Root<PerformanceHall> root,
		CriteriaBuilder cb, SearchFilterParam filterParam) {

		Integer min = filterParam.getMinSeats();
		Integer max = filterParam.getMaxSeats();

		if (min != null && max != null) {
			predicates.add(cb.between(root.get("totalSeats"), min, max));
		} else if (min != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("totalSeats"), min));
		} else if (max != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("totalSeats"), max));
		}
	}
}
