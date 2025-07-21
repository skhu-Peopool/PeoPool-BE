package com.example.peopoolbe.member.api.dto.request;

import jakarta.validation.constraints.Email;

public record MemberPwdForgotReq(
        @Email String email,
        String newPassword
) {
}
