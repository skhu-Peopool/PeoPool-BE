package com.example.peopoolbe.global.jwt.dto;

import lombok.Builder;

@Builder
public record TokenResDto(
        String accessToken,
        String refreshToken
) {
}
