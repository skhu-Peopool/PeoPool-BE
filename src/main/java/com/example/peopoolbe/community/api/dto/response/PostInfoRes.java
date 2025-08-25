package com.example.peopoolbe.community.api.dto.response;

import com.example.peopoolbe.community.domain.Category;
import com.example.peopoolbe.community.domain.Post;
import com.example.peopoolbe.community.domain.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PostInfoRes(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate endDate,
        Integer maxPeople,
        Status status,
        Category category,
        String image,
        Long writerId,
        String writerName
) {
    public static PostInfoRes from(Post post) {
        return PostInfoRes.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .title(post.getTitle())
                .content(post.getContent())
                .startDate(post.getRecruitmentStartDate())
                .endDate(post.getRecruitmentEndDate())
                .maxPeople(post.getMaximumPeople())
                .status(post.getStatus())
                .category(post.getCategory())
                .image(post.getImage())
                .writerId(post.getMember().getId())
                .writerName(post.getMember().getNickname())
                .build();
    }
}
