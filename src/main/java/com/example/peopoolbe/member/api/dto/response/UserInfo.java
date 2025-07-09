package com.example.peopoolbe.member.api.dto.response;

import com.example.peopoolbe.member.domain.Member;
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
    public static UserInfo from(Member member) {
        return UserInfo.builder()
                .userId(member.getUserId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .email(member.getEmail())
                .profileVisible(member.getProfileVisible())
                .build();
    }
}
