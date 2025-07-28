package com.example.peopoolbe.community.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PostAddReq(
        String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") LocalDateTime startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") LocalDateTime endDate,
        Integer maxPeople
) {
}
