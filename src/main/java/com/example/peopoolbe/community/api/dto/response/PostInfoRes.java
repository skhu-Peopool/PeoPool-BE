package com.example.peopoolbe.community.api.dto.response;

import com.example.peopoolbe.community.domain.Post;
import com.example.peopoolbe.community.domain.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostInfoRes(
        Long id,
        String title,
        String content,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer maxPeople,
        Status status,
        String writerName
) {
    public static PostInfoRes from(Post post) {
        return PostInfoRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .startDate(post.getRecruitmentStartDate())
                .endDate(post.getRecruitmentEndDate())
                .maxPeople(post.getMaximumPeople())
                .status(post.getStatus())
                .writerName(post.getMember().getNickname())
                .build();
    }
}
