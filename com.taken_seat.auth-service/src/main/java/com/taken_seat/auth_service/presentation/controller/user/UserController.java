package com.taken_seat.auth_service.presentation.controller.user;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.application.service.user.UserService;
import com.taken_seat.auth_service.presentation.dto.user.UserUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUser(@PathVariable UUID userId) {
        UserInfoResponseDto userInfo = userService.getUser(userId);

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUserDetails(@PathVariable UUID userId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        UserInfoResponseDto userInfo = userService.getUserDetails(userId, page, size);

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<UserInfoResponseDto>> searchUser(@RequestParam(required = false) String q,
                                                               @RequestParam(required = false) String role,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        PageResponseDto<UserInfoResponseDto> userInfo = userService.searchUser(q, role, page, size);

        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDto> updateUser(@PathVariable UUID userId,
                                                          @Valid @RequestBody UserUpdateRequestDto requestDto){

        UserInfoResponseDto userInfo = userService.updateUser(userId, requestDto.toDto());

        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
