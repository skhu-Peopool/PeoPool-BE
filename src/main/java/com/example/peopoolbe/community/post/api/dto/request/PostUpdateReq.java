package com.example.peopoolbe.community.post.api.dto.request;

import com.example.peopoolbe.community.post.domain.Category;
import com.example.peopoolbe.community.post.domain.PostStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PostUpdateReq(
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate recruitmentStartDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate recruitmentEndDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate activityStartDate,
        Integer maxPeople,
        PostStatus postStatus,
        Category category
) {
}
