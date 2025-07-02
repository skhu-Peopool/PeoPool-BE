package com.example.peopoolbe.global.jwt.dto;

import lombok.Builder;

@Builder
public record AccTokenResDto(
        String accessToken
) {
}
