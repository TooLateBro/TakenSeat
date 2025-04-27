package com.taken_seat.performance_service.performancehall.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.taken_seat.performance_service.performancehall.application.dto.command.CreatePerformanceHallCommand;
import com.taken_seat.performance_service.performancehall.application.dto.command.CreateSeatCommand;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallCreateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallCreateSeatDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HallCreateCommandMapper {

	CreatePerformanceHallCommand toCommand(HallCreateRequestDto hallCreateRequestDto);

	CreateSeatCommand toCommand(HallCreateSeatDto hallCreateSeatDto);
}
