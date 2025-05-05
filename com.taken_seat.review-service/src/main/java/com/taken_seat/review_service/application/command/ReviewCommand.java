package com.taken_seat.review_service.application.command;

public interface ReviewCommand<R> {
	R execute();
}
