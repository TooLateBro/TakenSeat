package com.taken_seat.performance_service.performance.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceCommand;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performance.application.dto.command.UpdateScheduleSeatCommand;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdateScheduleSeatDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PerformanceUpdateCommandMapper {

	UpdatePerformanceCommand toCommand(UpdateRequestDto updateRequestDto);

	UpdatePerformanceScheduleCommand toCommand(UpdatePerformanceScheduleDto updatePerformanceScheduleDto);

	UpdateScheduleSeatCommand toCommand(UpdateScheduleSeatDto updateSeatPriceDto);
}
