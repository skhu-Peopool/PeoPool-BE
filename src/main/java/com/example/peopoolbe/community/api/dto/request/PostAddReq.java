package com.example.peopoolbe.community.api.dto.request;

import com.example.peopoolbe.community.domain.Category;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PostAddReq(
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate recruitmentStartDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate recruitmentEndDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate activityStartDate,
        Integer maxPeople,
        Category category
) {
}
