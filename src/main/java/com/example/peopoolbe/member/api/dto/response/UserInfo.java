package com.example.peopoolbe.member.api.dto.response;

import lombok.Builder;

@Builder
public record UserInfo(
        String userId,
        String nickname,
        String profileImage,
        String email
){
}
