package com.taken_seat.coupon_service.infrastructure.config;

import com.taken_seat.common_service.aop.MetricTimerAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class MetricConfig {

	private final MeterRegistry meterRegistry;

	public MetricConfig(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@Bean
	public MetricTimerAspect metricTimerAspect() {
		return new MetricTimerAspect(meterRegistry);
	}
}