package com.taken_seat.auth_service.presentation.controller.mileage;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.application.service.mileage.MileageService;
import com.taken_seat.auth_service.presentation.docs.MileageControllerDocs;
import com.taken_seat.auth_service.presentation.dto.mileage.UserMileageRequestDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/mileages")
public class MileageController implements MileageControllerDocs {

    private final MileageService mileageService;

    public MileageController(MileageService mileageService) {
        this.mileageService = mileageService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponseData<UserMileageResponseDto>> createMileageToUser(AuthenticatedUser authenticatedUser,
                                                                                       @PathVariable UUID userId,
                                                                                       @RequestBody UserMileageRequestDto requestDto) {
        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }

        UserMileageResponseDto mileageInfo = mileageService.createMileageUser(userId, requestDto.toDto());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseData.success(mileageInfo));
    }

    @GetMapping("/{mileageId}")
    public ResponseEntity<ApiResponseData<UserMileageResponseDto>> getMileageUser(AuthenticatedUser authenticatedUser,
                                                                                  @PathVariable UUID mileageId) {
        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        UserMileageResponseDto mileageInfo = mileageService.getMileageUser(mileageId);
        return ResponseEntity.ok(ApiResponseData.success(mileageInfo));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponseData<PageResponseDto<UserMileageResponseDto>>> getMileageHistoryUser(AuthenticatedUser authenticatedUser,
                                                                                                          @RequestParam(defaultValue = "0") int page,
                                                                                                          @RequestParam(defaultValue = "10") int size,
                                                                                                          @PathVariable UUID userId) {

        if (authenticatedUser.getRole() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }

        PageResponseDto<UserMileageResponseDto> mileageInfo = mileageService.getMileageHistoryUser(userId, page, size);
        return ResponseEntity.ok(ApiResponseData.success(mileageInfo));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseData<PageResponseDto<UserMileageResponseDto>>> searchMileageUser(AuthenticatedUser authenticatedUser,
                                                                                                      @RequestParam(required = false) Integer startCount,
                                                                                                      @RequestParam(required = false) Integer endCount,
                                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                                      @RequestParam(defaultValue = "10") int size) {

        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        PageResponseDto<UserMileageResponseDto> mileageInfo = mileageService.searchMileageUser(
                startCount, endCount, page, size
        );
        return ResponseEntity.ok(ApiResponseData.success(mileageInfo));
    }

    @PatchMapping("/{mileageId}")
    public ResponseEntity<ApiResponseData<UserMileageResponseDto>> updateMileageUser(@PathVariable UUID mileageId,
                                                                                     AuthenticatedUser authenticatedUser,
                                                                                     @RequestBody UserMileageRequestDto requestDto) {

        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        UserMileageResponseDto mileageInfo = mileageService.updateMileageUser(mileageId, authenticatedUser, requestDto.toDto());
        return ResponseEntity.ok(ApiResponseData.success(mileageInfo));
    }

    @DeleteMapping("/{mileageId}")
    public ResponseEntity<ApiResponseData<Void>> deleteMileageUser(@PathVariable UUID mileageId,
                                                                   AuthenticatedUser authenticatedUser) {
        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        mileageService.deleteMileageUser(mileageId, authenticatedUser);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}
