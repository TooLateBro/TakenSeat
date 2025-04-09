package com.taken_seat.coupon_service.infrastructure.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.entity.QCoupon;
import com.taken_seat.coupon_service.domain.repository.CouponQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouponQueryRepositoryImpl implements CouponQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Coupon> findAllByDeletedAtIsNull(String name, Pageable pageable) {
        QCoupon coupon = QCoupon.coupon;

        // 콘텐츠 조회
        List<Coupon> content = jpaQueryFactory
                .selectFrom(coupon)
                .where(
                        coupon.deletedAt.isNull(),
                        searchCondition(name, coupon))
                .orderBy(coupon.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회
        Long total = jpaQueryFactory
                .select(coupon.count())
                .from(coupon)
                .where(coupon.deletedAt.isNull())
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression searchCondition(String name, QCoupon coupon) {
        return (name != null) ? coupon.name.contains(name) : null;
    }
}
