package com.taken_seat.auth_service.presentation.controller.mileage;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.mileage.MileageMapper;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.application.service.mileage.MileageService;
import com.taken_seat.auth_service.presentation.docs.MileageControllerDocs;
import com.taken_seat.auth_service.presentation.dto.mileage.UserMileageRequestDto;
import com.taken_seat.common_service.aop.annotation.RoleCheck;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/mileages")
@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER})
public class MileageController implements MileageControllerDocs {

    private final MileageService mileageService;
    private final MileageMapper mileageMapper;

    public MileageController(MileageService mileageService, MileageMapper mileageMapper) {
        this.mileageService = mileageService;
        this.mileageMapper = mileageMapper;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponseData<UserMileageResponseDto>> createMileageToUser(
            AuthenticatedUser authenticatedUser,
            @PathVariable UUID userId,
            @RequestBody UserMileageRequestDto requestDto) {

        UserMileageResponseDto mileageInfo = mileageService.createMileageUser(userId, mileageMapper.toDto(requestDto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseData.success(mileageInfo));
    }

    @GetMapping("/{mileageId}")
    public ResponseEntity<ApiResponseData<UserMileageResponseDto>> getMileageUser(
            AuthenticatedUser authenticatedUser,
            @PathVariable UUID mileageId) {

        UserMileageResponseDto mileageInfo = mileageService.getMileageUser(mileageId);
        return ResponseEntity.ok(ApiResponseData.success(mileageInfo));
    }

    @GetMapping("/history/{userId}")
    @RoleCheck() // 빈 배열은 모든 역할 허용, 메서드에 구현하면 우선순위가 더 높다.
    public ResponseEntity<ApiResponseData<PageResponseDto<UserMileageResponseDto>>> getMileageHistoryUser(
            AuthenticatedUser authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID userId) {
        if (!authenticatedUser.getUserId().equals(userId)) {
            throw new AuthException(ResponseCode.USER_NOT_FOUND);
        }

        PageResponseDto<UserMileageResponseDto> mileageInfo = mileageService.getMileageHistoryUser(userId, page, size);
        return ResponseEntity.ok(ApiResponseData.success(mileageInfo));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseData<PageResponseDto<UserMileageResponseDto>>> searchMileageUser(
            AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) Integer startCount,
            @RequestParam(required = false) Integer endCount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDto<UserMileageResponseDto> mileageInfo = mileageService.searchMileageUser(
                startCount, endCount, page, size
        );
        return ResponseEntity.ok(ApiResponseData.success(mileageInfo));
    }

    @PatchMapping("/{mileageId}")
    public ResponseEntity<ApiResponseData<UserMileageResponseDto>> updateMileageUser(
            @PathVariable UUID mileageId,
            AuthenticatedUser authenticatedUser,
            @RequestBody UserMileageRequestDto requestDto) {

        UserMileageResponseDto mileageInfo = mileageService.updateMileageUser(mileageId, authenticatedUser, mileageMapper.toDto(requestDto));
        return ResponseEntity.ok(ApiResponseData.success(mileageInfo));
    }

    @DeleteMapping("/{mileageId}")
    public ResponseEntity<ApiResponseData<Void>> deleteMileageUser(
            @PathVariable UUID mileageId,
            AuthenticatedUser authenticatedUser) {

        mileageService.deleteMileageUser(mileageId, authenticatedUser);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}