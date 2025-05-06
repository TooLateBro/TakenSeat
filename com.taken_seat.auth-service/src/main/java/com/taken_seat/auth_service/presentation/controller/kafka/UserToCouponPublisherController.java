package com.taken_seat.auth_service.presentation.controller.kafka;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.auth_service.application.kafka.coupon.UserToCouponPublisher;
import com.taken_seat.auth_service.presentation.docs.UserToCouponPublisherControllerDocs;
import com.taken_seat.auth_service.scheduler.UserSnapshotScheduler;
import com.taken_seat.common_service.aop.annotation.RoleCheck;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;

@RestController
@RequestMapping("/api/v1/users")
public class UserToCouponPublisherController implements UserToCouponPublisherControllerDocs {

	private final UserToCouponPublisher userToCouponPublisher;
	private final UserSnapshotScheduler userSnapshotScheduler;

	public UserToCouponPublisherController(UserToCouponPublisher userToCouponPublisher,
		UserSnapshotScheduler userSnapshotScheduler) {
		this.userToCouponPublisher = userToCouponPublisher;
		this.userSnapshotScheduler = userSnapshotScheduler;
	}

	@PostMapping("/send")
	@RoleCheck()
	public void sendUserCoupon(@RequestBody KafkaUserInfoMessage message, AuthenticatedUser authenticatedUser) {
		try {
			if (authenticatedUser == null) {
				throw new AuthException(ResponseCode.USER_NOT_FOUND);
			}
			userToCouponPublisher.sendUserCoupon(message);
		} catch (Exception e) {
			throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
		}
	}

	@GetMapping("/test/snapshot")
	public ResponseEntity<Void> triggerSnapshot() {
		userSnapshotScheduler.publishDailyUserSnapshot();
		return ResponseEntity.ok().build();
	}
}
