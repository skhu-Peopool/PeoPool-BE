package com.example.peopoolbe.member.api.dto.response;

import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.ViewStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserInfo(
        Long id,
        LocalDateTime createdAt,
        String nickname,
        String profileImage,
        String email,
        String introduction,
        String hashtag,
        LocalDate birthday,
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
                .introduction(member.getIntroduction())
                .hashtag(member.getHashtag())
                .birthday(member.getBirthday())
                .profileVisible(member.getProfileVisible())
                .activityVisible(member.getActivityVisible())
                .postVisible(member.getPostVisible())
                .build();
    }
}
