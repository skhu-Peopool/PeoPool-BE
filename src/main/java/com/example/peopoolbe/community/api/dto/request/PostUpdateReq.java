package com.example.peopoolbe.community.api.dto.request;

import com.example.peopoolbe.community.domain.Category;
import com.example.peopoolbe.community.domain.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PostUpdateReq(
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate endDate,
        Integer maxPeople,
        Status status,
        Category category
) {
}
