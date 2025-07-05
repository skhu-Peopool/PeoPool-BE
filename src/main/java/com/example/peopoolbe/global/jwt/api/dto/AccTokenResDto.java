package com.example.peopoolbe.global.jwt.api.dto;

import lombok.Builder;

@Builder
public record AccTokenResDto(
        String accessToken
) {
}
