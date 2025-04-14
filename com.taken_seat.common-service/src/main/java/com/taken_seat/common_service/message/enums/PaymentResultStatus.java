package com.taken_seat.common_service.message.enums;

public enum PaymentResultStatus {
	SUCCESS,                // 결제 성공
	INSUFFICIENT_BALANCE,   // 잔고 부족
	INVALID_PRICE,          // 잘못된 가격
	COUPON_OR_MILEAGE_FAIL  // 마일리지 또는 쿠폰 사용 실패
}