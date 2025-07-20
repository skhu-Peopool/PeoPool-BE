package com.example.peopoolbe.member.api.dto.request;

import jakarta.validation.constraints.Email;

public record MemberLoginReq(
        @Email String email,
        String password
) {
}
