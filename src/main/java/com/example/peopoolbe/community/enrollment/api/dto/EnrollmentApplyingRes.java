package com.example.peopoolbe.community.enrollment.api.dto;

import com.example.peopoolbe.community.enrollment.domain.Enrollment;
import com.example.peopoolbe.community.enrollment.domain.EnrollmentStatus;
import com.example.peopoolbe.community.post.domain.Category;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EnrollmentApplyingRes(
        Long enrollmentId,
        Long memberId,
        Long postId,
        String postTitle,
        Category postCategory,
        String comment,
        EnrollmentStatus status,
        LocalDateTime appliedAt,
        String memberNickname,
        String memberEmail,
        String memberProfileImage,
        String memberTags
) {
    public static EnrollmentApplyingRes from(Enrollment enrollment) {
        return EnrollmentApplyingRes.builder()
                .enrollmentId(enrollment.getId())
                .memberId(enrollment.getMember().getId())
                .postId(enrollment.getPost().getId())
                .postTitle(enrollment.getPost().getTitle())
                .postCategory(enrollment.getPost().getCategory())
                .comment(enrollment.getComment())
                .status(enrollment.getStatus())
                .appliedAt(enrollment.getCreatedAt())
                .memberNickname(enrollment.getMember().getNickname())
                .memberEmail(enrollment.getMember().getEmail())
                .memberProfileImage(enrollment.getMember().getProfileImage() == null ? null : enrollment.getMember().getProfileImage().getPath())
                .memberTags(enrollment.getMember().getHashtag())
                .build();
    }
}
