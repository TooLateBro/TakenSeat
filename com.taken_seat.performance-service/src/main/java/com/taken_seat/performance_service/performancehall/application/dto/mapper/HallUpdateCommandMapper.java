package com.taken_seat.performance_service.performancehall.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.taken_seat.performance_service.performancehall.application.dto.command.UpdatePerformanceHallCommand;
import com.taken_seat.performance_service.performancehall.application.dto.command.UpdateSeatCommand;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SeatDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HallUpdateCommandMapper {

	UpdatePerformanceHallCommand toCommand(UpdateRequestDto updateRequestDto);

	UpdateSeatCommand toCommand(SeatDto seatDto);
}
