package com.taken_seat.auth_service.presentation.docs;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserDetailsResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;
import com.taken_seat.auth_service.presentation.dto.user.UserUpdateRequestDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "유저", description = "검색, 수정, 삭제를 담당하는 API 입니다.")
public interface UserControllerDocs {

    @GetMapping("/api/v1/users/{userId}")
    @Operation(summary = "유저 단건 조회", description = "유저 정보만 단건 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 단건 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoResponseDtoV1.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<UserInfoResponseDtoV1>> getUser(@PathVariable UUID userId,
                                                                   AuthenticatedUser authenticatedUser);

    // 쿠폰 발급에 성공한 쿠폰인지 아닌지 확인하는 API
    @GetMapping("/api/v1/users/status/{couponId}")
    @Operation(summary = "유저 발급 쿠폰", description = "유저 발급 요청한 쿠폰의 성공 유무를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 발급 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "쿠폰 발급 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<String>> getCoupon(@PathVariable UUID couponId,
                                                      AuthenticatedUser authenticatedUser);

    @GetMapping("/api/v1/users/details/{userId}")
    @Operation(summary = "유저 단건 상세 조회", description = "유저 단건 정보와 유저가 보유한 마일리지 및 쿠폰을 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 단건 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoResponseDtoV1.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<UserDetailsResponseDtoV1>> getUserDetails(@PathVariable UUID userId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             AuthenticatedUser authenticatedUser);

    @GetMapping("/api/v1/users/search")
    @Operation(summary = "유저 전체 조회", description = "전체 유저를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 전체 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDetailsResponseDtoV1.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<PageResponseDto<UserDetailsResponseDtoV1>>> searchUser(@RequestParam(required = false) String q,
                                                                                       @RequestParam(required = false) String role,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size,
                                                                                       AuthenticatedUser authenticatedUser);

    @PatchMapping("/api/v1/users/{userId}")
    @Operation(summary = "유저 정보 수정", description = "유저 정보를 수정하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoResponseDtoV1.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<UserInfoResponseDtoV1>> updateUser(@PathVariable UUID userId,
                                                                      @Valid @RequestBody UserUpdateRequestDto requestDto,
                                                                      AuthenticatedUser authenticatedUser);

    @DeleteMapping("/api/v1/users/{userId}")
    @Operation(summary = "유저 삭제", description = "유저를 삭제 하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 삭제 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoResponseDtoV1.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<Void>> deleteUser(@PathVariable UUID userId,
                                                     AuthenticatedUser authenticatedUser);
}
