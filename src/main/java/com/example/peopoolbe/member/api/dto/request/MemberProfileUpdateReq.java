package com.example.peopoolbe.member.api.dto.request;

import com.example.peopoolbe.member.domain.ViewStatus;

public record MemberProfileUpdateReq(
        String nickname,
        ViewStatus viewStatus
) {
}
