package com.example.peopoolbe.community.api.dto.request;

import com.example.peopoolbe.community.domain.Category;
import com.example.peopoolbe.community.domain.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PostUpdateReq(
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") LocalDateTime startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") LocalDateTime endDate,
        Integer maxPeople,
        Status status,
        Category category
) {
}
