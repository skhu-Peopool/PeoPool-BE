package com.example.peopoolbe.global.jwt.api.dto;

import com.example.peopoolbe.member.domain.ViewStatus;
import lombok.Builder;

@Builder
public record AccessTokenAndUserInfo (
        String accessToken,
        String nickname,
        String profileImage,
        String email,
        ViewStatus profileVisible,
        ViewStatus activityVisible,
        ViewStatus postVisible
){
}
