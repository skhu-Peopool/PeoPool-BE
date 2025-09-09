package com.example.peopoolbe.community.enrollment.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record EnrollmentApplyingList(
        List<EnrollmentApplyingRes> enrollmentApplyingList
) {
    public static EnrollmentApplyingList fromApplyingRes(List<EnrollmentApplyingRes> applyingRes) {
        return EnrollmentApplyingList.builder()
                .enrollmentApplyingList(applyingRes)
                .build();
    }
}
