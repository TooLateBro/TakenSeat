package com.taken_seat.common_service.dto.response.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "BookingSeatClientResponse",
	description = "좌석 예약 응답 DTO"
)
public interface BookingSeatClientResponseSchema {

	@Schema(
		description = "좌석 가격",
		example = "50000"
	)
	Integer price();

	@Schema(
		description = "예약 가능 여부",
		example = "true"
	)
	boolean reserved();

	@Schema(
		description = "예외 발생 사유 (예약 불가 시)",
		example = "이미 예약된 좌석입니다."
	)
	String reason();
}
