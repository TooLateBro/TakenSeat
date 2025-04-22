package com.taken_seat.performance_service.common.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;

public class QueryDslOrderUtil {

	public static <T> OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, Class<T> clazz, String alias) {
		PathBuilder<T> pathBuilder = new PathBuilder<>(clazz, alias);
		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

		for (Sort.Order order : sort) {
			Order direction = order.isAscending() ? Order.ASC : Order.DESC;
			orderSpecifiers.add(new OrderSpecifier<>(
				direction,
				pathBuilder.get(order.getProperty(), Comparable.class)
			));
		}

		return orderSpecifiers.toArray(new OrderSpecifier[0]);
	}
}
