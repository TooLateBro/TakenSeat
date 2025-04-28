package com.taken_seat.performance_service.performancehall.presentation.dto.request.schema;

import java.util.List;

import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallCreateSeatDto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공연장 생성 요청 DTO
 */
@Schema(
	name = "HallCreateRequest",
	description = "공연장 생성 요청 DTO"
)
public interface HallCreateRequestSchema {

	@Schema(
		description = "공연장 이름",
		example = "서울 올림픽홀"
	)
	String name();

	@Schema(
		description = "공연장 주소",
		example = "서울특별시 송파구 올림픽로 424"
	)
	String address();

	@Schema(
		description = "공연장 총 좌석 수",
		example = "5000"
	)
	Integer totalSeats();

	@Schema(
		description = "공연장 설명",
		example = "최고의 공연장입니다."
	)
	String description();

	@Schema(
		description = "공연장 좌석 정보 리스트",
		implementation = HallCreateSeatDto.class
	)
	List<HallCreateSeatDto> seats();
}
