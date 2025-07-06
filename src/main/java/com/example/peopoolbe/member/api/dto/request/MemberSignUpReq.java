package com.example.peopoolbe.member.api.dto.request;

import jakarta.validation.constraints.Email;

public record MemberSignUpReq(
    String userId,
    String password,
    String nickname,
    @Email String email
){
}
