package com.example.peopoolbe.member.api.dto.request;

public record MemberSignUpReq(
    String userId,
    String password,
    String name,
    String email
){
}
