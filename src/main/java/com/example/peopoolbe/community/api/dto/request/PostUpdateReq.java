package com.example.peopoolbe.community.api.dto.request;

import com.example.peopoolbe.community.domain.Status;

import java.util.Date;

public record PostUpdateReq(
        String title,
        String content,
        Date startDate,
        Date endDate,
        Integer maxPeople,
        Status status
) {
}
