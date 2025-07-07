package com.example.peopoolbe.member.api.dto.response;

import com.example.peopoolbe.member.domain.ProfileVisible;
import lombok.Builder;

@Builder
public record UserInfo(
        String userId,
        String nickname,
        String profileImage,
        String email,
        ProfileVisible profileVisible
){
}
