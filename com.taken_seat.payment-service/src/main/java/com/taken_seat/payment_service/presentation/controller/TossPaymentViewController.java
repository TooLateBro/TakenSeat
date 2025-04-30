package com.taken_seat.payment_service.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TossPaymentViewController {

	@GetMapping("/checkout")
	public String getCheckoutPage(@RequestParam String bookingId) {
		return "tossPaymentCheckout";
	}

	@GetMapping("/success")
	public String getSuccessPage() {
		return "tossPaymentSuccess";
	}

}
