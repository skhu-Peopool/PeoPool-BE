package com.example.peopoolbe.community.enrollment.api.dto;

import com.example.peopoolbe.community.enrollment.domain.Enrollment;
import com.example.peopoolbe.community.enrollment.domain.EnrollmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EnrollmentApplyingRes(
        Long enrollmentId,
        Long memberId,
        Long postId,
        String comment,
        EnrollmentStatus status,
        LocalDateTime appliedAt,
        String memberNickname,
        String memberEmail,
        String memberProfileImage
) {
    public static EnrollmentApplyingRes from(Enrollment enrollment) {
        return EnrollmentApplyingRes.builder()
                .enrollmentId(enrollment.getId())
                .memberId(enrollment.getMember().getId())
                .postId(enrollment.getPost().getId())
                .comment(enrollment.getComment())
                .status(enrollment.getStatus())
                .appliedAt(enrollment.getCreatedAt())
                .memberNickname(enrollment.getMember().getNickname())
                .memberEmail(enrollment.getMember().getEmail())
                .memberProfileImage(enrollment.getMember().getProfileImage() == null ? null : enrollment.getMember().getProfileImage().getPath())
                .build();
    }
}
