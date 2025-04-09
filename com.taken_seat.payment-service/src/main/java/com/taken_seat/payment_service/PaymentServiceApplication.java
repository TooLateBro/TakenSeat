package com.taken_seat.payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.taken_seat.common_service.exception.handler.GlobalExceptionHandler;

@SpringBootApplication
@Import({
	GlobalExceptionHandler.class
})
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
