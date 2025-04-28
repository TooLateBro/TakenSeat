package com.taken_seat.auth_service.presentation.docs;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;
import com.taken_seat.auth_service.presentation.dto.auth.AuthLoginRequestDto;
import com.taken_seat.auth_service.presentation.dto.auth.AuthSignUpRequestDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "사용자", description = "회원가입, 로그인, 로그아웃을 담당하는 API 입니다.")
public interface AuthControllerDocs {

    @PostMapping("/api/v1/auths/signUp")
    @Operation(
            summary = "회원가입",
            description = "로그아웃을 담당하는 API 입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoResponseDtoV1.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 이메일",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<UserInfoResponseDtoV1>> signUp(@Valid @RequestBody AuthSignUpRequestDto requestDto);

    @PostMapping("/api/v1/auths/login")
    @Operation(summary = "로그인", description = "로그인을 담당하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = AuthLoginResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<AuthLoginResponseDto>> login(@Valid @RequestBody AuthLoginRequestDto requestDto);

    @PostMapping("/api/v1/auths/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 담당하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 토큰",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<Void>> logout(@RequestHeader(value = "Authorization") String token);
}
