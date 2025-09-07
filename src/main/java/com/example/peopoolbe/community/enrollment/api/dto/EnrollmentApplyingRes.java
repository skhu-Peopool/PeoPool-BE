package com.example.peopoolbe.community.enrollment.api.dto;

import com.example.peopoolbe.community.enrollment.domain.Enrollment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EnrollmentApplyingRes(
        Long memberId,
        Long postId,
        LocalDateTime appliedAt
) {
    public static EnrollmentApplyingRes from(Enrollment enrollment) {
        return EnrollmentApplyingRes.builder()
                .memberId(enrollment.getMember().getId())
                .postId(enrollment.getPost().getId())
                .appliedAt(enrollment.getCreatedAt())
                .build();
    }
}
