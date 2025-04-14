package com.taken_seat.auth_service.infrastructure.persistence.mileage;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.mileage.QMileage;
import com.taken_seat.auth_service.domain.entity.user.QUser;
import com.taken_seat.auth_service.domain.repository.mileage.MileageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MileageQueryRepositoryImpl implements MileageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Mileage> findAllByDeletedAtIsNull(Integer startCount, Integer endCount, Pageable pageable) {
        if (startCount != null && endCount != null && startCount > endCount) {
            throw new IllegalArgumentException("startCount must be less than or equal to endCount");
        }
        QUser user = QUser.user;
        QMileage mileage = QMileage.mileage1;

        // 서브쿼리: count 조건을 만족하는 mileage 레코드의 user.id 조회
        JPAQuery<UUID> subQuery = jpaQueryFactory
                .select(mileage.user.id)
                .from(mileage)
                .where(
                        mileage.deletedAt.isNull(),
                        mileageCountBetween(startCount, endCount, mileage)
                )
                .groupBy(mileage.user.id);

        // 콘텐츠 조회
        List<Mileage> content = jpaQueryFactory
                .selectFrom(mileage)
                .join(mileage.user, user)
                .where(
                        isNotDeleted(mileage),
                        mileage.user.id.in(subQuery),
                        mileageCountBetween(startCount, endCount, mileage)  // 여기서도 조건 적용
                )
                .orderBy(mileage.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회
        Long total = jpaQueryFactory
                .select(mileage.count())
                .from(mileage)
                .where(
                        isNotDeleted(mileage),
                        mileage.user.id.in(subQuery),
                        mileageCountBetween(startCount, endCount, mileage)  // 여기서도 조건 적용
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    // 삭제되지 않은 조건
    private BooleanExpression isNotDeleted(QMileage mileage) {
        return mileage.deletedAt.isNull();
    }

    private BooleanExpression mileageCountBetween(Integer startCount, Integer endCount, QMileage mileage) {
        if (startCount == null && endCount == null) {
            return null;
        }
        BooleanExpression condition = null;
        if (startCount != null) {
            condition = mileage.mileage.goe(startCount);
        }
        if (endCount != null) {
            BooleanExpression endCondition = mileage.mileage.loe(endCount);
            condition = condition == null ? endCondition : condition.and(endCondition);
        }
        return condition;
    }
}
