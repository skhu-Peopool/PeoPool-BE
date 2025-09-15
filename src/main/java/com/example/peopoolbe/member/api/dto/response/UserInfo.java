package com.example.peopoolbe.member.api.dto.response;

import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.ViewStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserInfo(
        Long id,
        LocalDateTime createdAt,
        String nickname,
        String profileImage,
        String email,
        String mainIntroduction,
        String subIntroduction,
        String hashtag,
        String kakaoId,
        ViewStatus profileVisible,
        ViewStatus activityVisible,
        ViewStatus postVisible
){
    public static UserInfo from(Member member) {
        return UserInfo.builder()
                .id(member.getId())
                .createdAt(member.getCreatedAt())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .email(member.getEmail())
                .mainIntroduction(member.getMainIntroduction())
                .subIntroduction(member.getSubIntroduction())
                .hashtag(member.getHashtag())
                .kakaoId(member.getKakaoId())
                .profileVisible(member.getProfileVisible())
                .activityVisible(member.getActivityVisible())
                .postVisible(member.getPostVisible())
                .build();
    }
}
