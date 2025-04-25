package com.taken_seat.auth_service.infrastructure.persistence.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.taken_seat.auth_service.domain.entity.mileage.QMileage;
import com.taken_seat.auth_service.domain.entity.user.QUser;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserQueryRepository;
import com.taken_seat.common_service.aop.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<User> findAllByDeletedAtIsNull(String username, String role, Pageable pageable) {
        QUser user = QUser.user;
        QMileage mileage = QMileage.mileage1;

        // 컨텐츠 조회
        List<User> content = jpaQueryFactory
                .selectFrom(user)
                .distinct() // 중복 제거
                .leftJoin(user.mileages, mileage).fetchJoin()
                .where(
                        isNotDeleted(user),
                        searchCondition(username, user), // 유저 이름 검색
                        roleEq(role, user), // 유저 권한 검색
                        isLatestMileage(user, mileage) // 서브 쿼리
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회
        Long count = jpaQueryFactory
                .select(user.countDistinct())
                .from(user)
                .where(
                        isNotDeleted(user),
                        searchCondition(username, user),
                        roleEq(role, user)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0);
    }

    // 삭제되지 않은 사용자 조건
    private BooleanExpression isNotDeleted(QUser user) {
        return user.deletedAt.isNull();
    }

    // 검색어 조건
    private BooleanExpression searchCondition(String username, QUser user) {
        return StringUtils.hasText(username) ?
                user.username.containsIgnoreCase(username) : null;
    }

    // 역할 조건
    private BooleanExpression roleEq(String role, QUser user) {
        return StringUtils.hasText(role) ?
                user.role.eq(Role.valueOf(role)) : null;
    }

    // 최신 마일리지 조건
    private BooleanExpression isLatestMileage(QUser user, QMileage mileage) {
        QMileage subMileage = new QMileage("subMileage");

        return mileage.id.in(
                JPAExpressions
                        .select(subMileage.id)
                        .from(subMileage)
                        .where(subMileage.user.eq(user))
                        .orderBy(subMileage.updatedAt.desc())
                        .limit(1)
        ).or(mileage.isNull());
    }
}