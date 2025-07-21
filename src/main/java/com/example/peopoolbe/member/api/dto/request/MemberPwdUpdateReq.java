package com.example.peopoolbe.member.api.dto.request;

public record MemberPwdUpdateReq(
        String password,
        String newPassword
) {
}
