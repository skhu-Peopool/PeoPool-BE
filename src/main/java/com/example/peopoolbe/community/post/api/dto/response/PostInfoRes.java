package com.example.peopoolbe.community.post.api.dto.response;

import com.example.peopoolbe.community.post.domain.Category;
import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.community.post.domain.PostStatus;
import com.example.peopoolbe.global.s3.domain.Image;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
public record PostInfoRes(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate recruitmentStartDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate recruitmentEndDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate activityStartDate,
        Integer maxPeople,
        Integer approvedPeople,
        Integer appliedPeople,
        PostStatus postStatus,
        Category category,
        String[] image,
        Integer views,
        Long writerId,
        String writerName,
        String writerProfileImage
) {
    public static PostInfoRes from(Post post) {
        return PostInfoRes.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .title(post.getTitle())
                .content(post.getContent())
                .recruitmentStartDate(post.getRecruitmentStartDate())
                .recruitmentEndDate(post.getRecruitmentEndDate())
                .activityStartDate(post.getActivityStartDate())
                .maxPeople(post.getMaximumPeople())
                .approvedPeople(post.getApprovedPeople())
                .appliedPeople(post.getAppliedPeople())
                .postStatus(post.getPostStatus())
                .category(post.getCategory())
                .image(post.getImage() == null ? null : post.getImage().stream().filter(Objects::nonNull).map(Image::getPath).toArray(String[]::new))
                .views(post.getViews())
                .writerId(post.getMember().getId())
                .writerName(post.getMember().getNickname())
                .writerProfileImage(post.getMember().getProfileImage() == null ? null : post.getMember().getProfileImage().getPath())
                .build();
    }
}
