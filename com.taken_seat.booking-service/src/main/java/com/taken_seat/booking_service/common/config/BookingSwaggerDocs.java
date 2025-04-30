package com.taken_seat.booking_service.common.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BookingSwaggerDocs {

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "예매하기", description = "공연, 회차, 좌석 정보로 예매를 진행합니다.")
	@ApiResponse(responseCode = "200", description = "예매 생성 성공")
	@interface CreateBooking {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "예매 취소하기", description = "예매 ID로 예매를 취소합니다. 결제했다면 환불가능 여부 확인 후 환불을 진행합니다.")
	@ApiResponse(responseCode = "204", description = "예매 취소 성공")
	@interface CancelBooking {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "예매 삭제하기", description = "예매 ID로 예매를 삭제합니다. 대기중인 예매는 삭제할 수 없습니다.")
	@ApiResponse(responseCode = "204", description = "예매 삭제 성공")
	@interface DeleteBooking {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "예매 결제하기", description = "예매 ID와 선택적으로 마일리지, 쿠폰을 사용하여 예매를 결제합니다.")
	@ApiResponse(responseCode = "204", description = "예매 결제 성공")
	@interface CreatePayment {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "예매 조회하기", description = "예매 ID로 예매를 조회합니다. 삭제된 예매는 조회할 수 없습니다.")
	@ApiResponse(responseCode = "200", description = "예매 조회 성공")
	@interface ReadBooking {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "예매 리스트 조회하기", description = "사용자 ID로 예매 리스트를 조회합니다. 삭제된 예매는 조회할 수 없습니다.")
	@ApiResponse(responseCode = "200", description = "예매 조회 성공")
	@interface ReadBookings {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "예매 상태하기", description = "사용자 ID와 공연 ID로 예매 상태를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "예매 상태 조회 성공")
	@interface GetBookingStatus {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "관리자 예매 조회하기", description = "예매 ID로 예매를 조회합니다. 삭제된 예매도 조회할 수 있습니다.")
	@ApiResponse(responseCode = "200", description = "예매 조회 성공")
	@interface AdminReadBooking {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "관리자 리스트 예매 조회하기", description = "조회할 사용자 ID로 예매를 조회합니다. 삭제된 예매도 조회할 수 있습니다.")
	@ApiResponse(responseCode = "200", description = "예매 조회 성공")
	@interface AdminReadBookings {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "티켓 조회하기", description = "티켓 ID로 조회합니다. 삭제된 티켓은 조회할 수 없습니다.")
	@ApiResponse(responseCode = "200", description = "티켓 조회 성공")
	@interface ReadTicket {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "티켓 리스트 조회하기", description = "예매 ID로 조회합니다. 삭제된 티켓은 조회할 수 없습니다.")
	@ApiResponse(responseCode = "200", description = "티켓 리스트 조회 성공")
	@interface ReadTickets {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "티켓 삭제하기", description = "티켓 ID로 삭제합니다.")
	@ApiResponse(responseCode = "204", description = "티켓 삭제 성공")
	@interface DeleteTicket {
	}
}