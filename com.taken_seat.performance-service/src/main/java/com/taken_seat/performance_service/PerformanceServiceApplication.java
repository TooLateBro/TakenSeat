package com.taken_seat.performance_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
	"com.taken_seat.performance_service",
	"com.taken_seat.common_service"
})
@EnableJpaAuditing
@EnableScheduling
public class PerformanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformanceServiceApplication.class, args);
	}

}