package com.example.peopoolbe.member.api.dto.request;

import jakarta.validation.constraints.Email;

public record MemberSignUpReq(
    @Email String email,
    String password,
    String nickname
){
}
