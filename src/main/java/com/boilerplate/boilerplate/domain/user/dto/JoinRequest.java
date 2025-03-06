package com.boilerplate.boilerplate.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "사용자 아이디를 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
//        message = "비밀번호는 8자 이상의 영문자, 숫자, 특수문자를 포함해야 합니다")
    private String password;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;
}
