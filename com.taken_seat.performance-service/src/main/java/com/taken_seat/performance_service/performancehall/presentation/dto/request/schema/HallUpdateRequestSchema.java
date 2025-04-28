package com.taken_seat.performance_service.performancehall.presentation.dto.request.schema;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallUpdateSeatDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallUpdateRequest",
	description = "공연장 수정 요청 DTO"
)
public interface HallUpdateRequestSchema {
	@Schema(
		description = "수정할 공연장 ID (UUID)",
		example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
	)
	UUID performanceHallId();

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
		description = "공연장 설명",
		example = "최대 5000석 규모의 대형 공연장"
	)
	String description();

	@Schema(
		description = "수정할 좌석 정보 리스트",
		implementation = HallUpdateSeatDto.class
	)
	List<HallUpdateSeatDto> seats();
}
