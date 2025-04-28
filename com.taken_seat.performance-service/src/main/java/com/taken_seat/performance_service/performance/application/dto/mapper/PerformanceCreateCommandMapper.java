package com.taken_seat.performance_service.performance.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.taken_seat.performance_service.performance.application.dto.command.CreatePerformanceCommand;
import com.taken_seat.performance_service.performance.application.dto.command.CreatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performance.application.dto.command.CreateScheduleSeatCommand;
import com.taken_seat.performance_service.performance.presentation.dto.request.CreatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.presentation.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.request.CreateScheduleSeatDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PerformanceCreateCommandMapper {

	CreatePerformanceCommand toCommand(CreateRequestDto createRequestDto);

	CreatePerformanceScheduleCommand toCommand(CreatePerformanceScheduleDto createPerformanceScheduleDto);

	CreateScheduleSeatCommand toCommand(CreateScheduleSeatDto createScheduleSeatDto);
}
