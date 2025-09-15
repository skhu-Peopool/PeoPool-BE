package com.example.peopoolbe.member.api.dto.request;

public record MemberProfileUpdateReq(
        String nickname,
        String mainIntroduction,
        String subIntroduction,
        String hashtag,
        String kakaoId
) {
}
