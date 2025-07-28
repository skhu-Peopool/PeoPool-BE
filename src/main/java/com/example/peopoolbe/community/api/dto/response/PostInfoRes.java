package com.example.peopoolbe.community.api.dto.response;

import com.example.peopoolbe.community.domain.Post;
import com.example.peopoolbe.community.domain.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
public record PostInfoRes(
        Long id,
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") LocalDateTime startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") LocalDateTime endDate,
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
