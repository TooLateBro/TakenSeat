package com.taken_seat.auth_service.presentation.docs;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.presentation.dto.mileage.UserMileageRequestDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "마일리지", description = "마일리지를 생성, 검색, 수정, 삭제 하는 API 입니다.")
public interface MileageControllerDocs {

    @PostMapping("/api/v1/users/mileages/{userId}")
    @Operation(summary = "마일리지 생성", description = "유저에게 마일리지를 생성해주는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "마일리지 생성 성공",
                    content = @Content(schema = @Schema(implementation = UserMileageResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<UserMileageResponseDto>> createMileageToUser(@Parameter(hidden = true) AuthenticatedUser authenticatedUser,
                                                                                @PathVariable UUID userId,
                                                                                @RequestBody UserMileageRequestDto requestDto);

    @GetMapping("/api/v1/users/mileages/{mileageId}")
    @Operation(summary = "마일리지 단건 조회", description = "유저가 보유한 마일리지 중 가장 최근에 업데이트 된 마일리지를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "마일리지 단건 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserMileageResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<UserMileageResponseDto>> getMileageUser(@Parameter(hidden = true) AuthenticatedUser authenticatedUser,
                                                                           @PathVariable UUID mileageId);

    @GetMapping("/api/v1/users/mileages/history/{userId}")
    @Operation(summary = "마일리지 히스토리 조회", description = "유저가 보유한 마일리지의 히스토리를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "마일리지 히스토리 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserMileageResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<PageResponseDto<UserMileageResponseDto>>> getMileageHistoryUser(@Parameter(hidden = true) AuthenticatedUser authenticatedUser,
                                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                                   @PathVariable UUID userId);

    @GetMapping("/api/v1/users/mileages/search")
    @Operation(summary = "마일리지 전체 조회", description = "전체 유저의 마일리지를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "마일리지 전체 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserMileageResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<PageResponseDto<UserMileageResponseDto>>> searchMileageUser(@Parameter(hidden = true) AuthenticatedUser authenticatedUser,
                                                                                               @RequestParam(required = false) Integer startCount,
                                                                                               @RequestParam(required = false) Integer endCount,
                                                                                               @RequestParam(defaultValue = "0") int page,
                                                                                               @RequestParam(defaultValue = "10") int size);

    @PatchMapping("/api/v1/users/mileages/{mileageId}")
    @Operation(summary = "마일리지 수정", description = "마일리지를 수정하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "마일리지 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserMileageResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<UserMileageResponseDto>> updateMileageUser(@PathVariable UUID mileageId,
                                                                              @Parameter(hidden = true) AuthenticatedUser authenticatedUser,
                                                                              @RequestBody UserMileageRequestDto requestDto);

    @DeleteMapping("/api/v1/users/mileages/{mileageId}")
    @Operation(summary = "마일리지 삭제", description = "마일리지를 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "마일리지 삭제 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<Void>> deleteMileageUser(@PathVariable UUID mileageId,
                                                            @Parameter(hidden = true) AuthenticatedUser authenticatedUser);
}
