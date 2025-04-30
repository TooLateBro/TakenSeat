package com.taken_seat.common_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Value;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class MetricTimerAspect {

	private final MeterRegistry meterRegistry;

	public MetricTimerAspect(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@Value("${spring.application.name:unknown}")
	private String serviceName;

	@Around("@annotation(track)")
	public Object around(ProceedingJoinPoint pjp, TrackLatency track) throws Throwable {
		Timer.Sample sample = Timer.start(meterRegistry);
		try {
			return pjp.proceed();
		} finally {
			sample.stop(
				Timer.builder(track.value())
					.description(track.description())
					.tag("service", serviceName)
					.register(meterRegistry)
			);
		}
	}
}

