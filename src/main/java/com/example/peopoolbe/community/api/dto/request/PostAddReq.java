package com.example.peopoolbe.community.api.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record PostAddReq(
        String title,
        String content,
        @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
        Integer maxPeople
) {
}
