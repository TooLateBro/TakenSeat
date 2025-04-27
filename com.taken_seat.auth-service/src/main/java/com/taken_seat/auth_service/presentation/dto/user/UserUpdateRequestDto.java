package com.taken_seat.auth_service.presentation.dto.user;

import com.taken_seat.auth_service.application.dto.user.UserUpdateDto;
import com.taken_seat.common_service.aop.vo.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    @Size(min = 4, max = 10, message = "이름은 최소 4자 이상, 10자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "이름은 알파벳 소문자(a~z)와 숫자(0~9)로만 구성되어야 합니다.")
    @Schema(example = "exampleUser")
    private String username = null;

    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).+$",
            message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    @Schema(example = "Password1!")
    private String password = null;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(example = "example@example.com")
    private String email = null;

    @Pattern(
            regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
            message = "전화번호는 형식에 맞게 입력해주세요. 예: 010-1234-5678"
    )
    @Schema(example = "010-1111-1111")
    private String phone = null;

    @Schema(example = "ADMIN")
    private Role role = null;

    public UserUpdateDto toDto() {
        return UserUpdateDto.create(this.username, this.email, this.phone, this.password, this.role);
    }
}
