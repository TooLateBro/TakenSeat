package com.taken_seat.auth_service.presentation.controller.mileage;

import com.taken_seat.auth_service.application.dto.mileage.UserMileageResponseDto;
import com.taken_seat.auth_service.application.service.mileage.MileageService;
import com.taken_seat.auth_service.presentation.dto.mileage.CreateUserMileageRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/mileages")
public class MileageController {

    private final MileageService mileageService;

    public MileageController(MileageService mileageService) {
        this.mileageService = mileageService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<UserMileageResponseDto> createMileageToUser(@RequestHeader("X-Role") String role,
                                               @PathVariable UUID userId,
                                               @RequestBody CreateUserMileageRequestDto requestDto) {

        if(role == null || !(role.equals("ADMIN") || role.equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserMileageResponseDto mileageInfo = mileageService.createMileageUser(userId, requestDto.toDto());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mileageInfo);
    }

//    @GetMapping
//    @GetMapping
//    @PatchMapping
//    @DeleteMapping
}
