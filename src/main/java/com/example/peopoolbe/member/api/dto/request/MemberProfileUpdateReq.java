package com.example.peopoolbe.member.api.dto.request;

import com.example.peopoolbe.member.domain.ProfileVisible;

public record MemberProfileUpdateReq(
        String password,
        String nickname,
        String profileImage,
        ProfileVisible profileVisible
) {
}
