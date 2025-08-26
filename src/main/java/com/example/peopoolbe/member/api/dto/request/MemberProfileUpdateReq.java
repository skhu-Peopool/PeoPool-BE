package com.example.peopoolbe.member.api.dto.request;

import java.time.LocalDate;

public record MemberProfileUpdateReq(
        String nickname,
        String introduction,
        String hashtag,
        LocalDate birthday
) {
}
