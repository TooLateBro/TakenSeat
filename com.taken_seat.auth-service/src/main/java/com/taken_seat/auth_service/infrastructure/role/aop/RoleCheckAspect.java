package com.taken_seat.auth_service.infrastructure.role.aop;

import com.taken_seat.auth_service.domain.vo.Role;
import com.taken_seat.auth_service.infrastructure.role.RoleCheck;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class RoleCheckAspect {

    // @RoleCheck 어노테이션이 메서드 또는 클래스에 붙은 경우를 가로챔
    @Around("@annotation(com.taken_seat.auth_service.infrastructure.role.RoleCheck) || " +
            "@within(com.taken_seat.auth_service.infrastructure.role.RoleCheck)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {

        // 현재 실행되는 메서드 정보 추출
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 메서드에 붙은 @RoleCheck 어노테이션 추출
        RoleCheck roleCheck = method.getAnnotation(RoleCheck.class);

        // 메서드에 어노테이션이 없으면 클래스에서 추출
        if (roleCheck == null) {
            roleCheck = joinPoint.getTarget().getClass().getAnnotation(RoleCheck.class);
        }

        // 메서드 인자 중에서 AuthenticatedUser 객체 탐색
        Object[] args = joinPoint.getArgs();
        AuthenticatedUser authenticatedUser = null;

        for (Object arg : args) {
            if (arg instanceof AuthenticatedUser) {
                authenticatedUser = (AuthenticatedUser) arg;
                break;
            }
        }

        // 인증된 사용자 정보가 없으면 접근 거부
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode(),
                            "접근 권한이 없습니다."));
        }

        // 사용자의 역할 정보를 enum으로 변환
        Role userRole = Role.valueOf(authenticatedUser.getRole());

        // 어노테이션에 설정된 허용 역할 목록 가져오기
        Role[] allowedRoles = roleCheck != null ? roleCheck.allowedRoles() : new Role[]{};

        // 아무 역할도 지정되지 않은 경우: 모두 허용
        if (allowedRoles.length == 0) {
            return joinPoint.proceed();
        }

        // 사용자 역할이 허용된 역할 목록에 있는지 확인
        boolean hasAllowedRole = Arrays.stream(allowedRoles)
                .anyMatch(role -> role == userRole);

        // 권한이 없는 경우: 접근 거부
        if (!hasAllowedRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode(),
                            "접근 권한이 없습니다."));
        }

        // 모든 조건 통과: 원래 메서드 실행
        return joinPoint.proceed();
    }
}