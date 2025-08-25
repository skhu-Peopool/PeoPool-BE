package com.example.peopoolbe.member.api.dto.request;

import com.example.peopoolbe.member.domain.ViewStatus;

import java.time.LocalDate;

public record MemberProfileUpdateReq(
        String nickname,
        String introduction,
        String hashtag,
        LocalDate birthday,
        ViewStatus profileVisible,
        ViewStatus activityVisible,
        ViewStatus postVisible
) {
}
