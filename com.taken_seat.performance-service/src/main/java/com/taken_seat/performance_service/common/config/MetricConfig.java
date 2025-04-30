package com.taken_seat.performance_service.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.taken_seat.common_service.aop.MetricTimerAspect;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@EnableAspectJAutoProxy
public class MetricConfig {

	@Bean
	public MetricTimerAspect metricTimerAspect(MeterRegistry meterRegistry) {
		return new MetricTimerAspect(meterRegistry);
	}
}
