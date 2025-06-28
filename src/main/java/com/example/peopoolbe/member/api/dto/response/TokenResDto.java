package com.example.peopoolbe.member.api.dto.response;

import lombok.Builder;

@Builder
public record TokenResDto(
        String accessToken
) {
}
