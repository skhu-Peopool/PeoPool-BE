package com.example.peopoolbe.member.api.dto.request;

public record MemberProfileUpdateReq(
        String password,
        String nickname,
        String profileImage,
        boolean isProfileVisible
) {
}
