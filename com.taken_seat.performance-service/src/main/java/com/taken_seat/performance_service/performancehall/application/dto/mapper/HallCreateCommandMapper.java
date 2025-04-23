package com.taken_seat.performance_service.performancehall.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.taken_seat.performance_service.performancehall.application.dto.command.CreatePerformanceHallCommand;
import com.taken_seat.performance_service.performancehall.application.dto.command.CreateSeatCommand;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.CreateSeatDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HallCreateCommandMapper {

	CreatePerformanceHallCommand toCommand(CreateRequestDto createRequestDto);

	CreateSeatCommand toCommand(CreateSeatDto createSeatDto);
}
