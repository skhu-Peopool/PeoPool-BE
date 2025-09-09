package com.example.peopoolbe.community.enrollment.api.dto.req;

import lombok.Builder;
import lombok.Getter;

@Builder
public record EnrollmentApplyingReq(
        String comment
) {
}
