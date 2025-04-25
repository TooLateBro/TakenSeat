package com.taken_seat.common_service.aop.annotation;

import com.taken_seat.common_service.aop.vo.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 어노테이션이 메서드와 클래스에 모두 적용될 수 있도록 지정
@Target({ElementType.METHOD, ElementType.TYPE})
// 어노테이션 정보를 런타임까지 유지 (AOP 에서 사용 가능)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleCheck {

    // 허용된 역할 목록 설정 (기본은 빈 배열 = 모든 역할 허용)
    Role[] allowedRoles() default {};
}
