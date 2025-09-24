package com.example.peopoolbe.global.sse.dto;

import com.example.peopoolbe.global.sse.domain.ActionType;
import com.example.peopoolbe.global.sse.domain.EventType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationRes(
        EventType eventType,
        ActionType actionType,
        Long targetId,
        String senderName,
        String message,
        String timeAgo
) {
    public static NotificationRes of(
            EventType eventType,
            ActionType actionType,
            Long targetId,
            String senderName,
            String message,
            LocalDateTime createdAt
    ) {
        return NotificationRes.builder()
                .eventType(eventType)
                .actionType(actionType)
                .targetId(targetId)
                .senderName(senderName)
                .message(message)
                .timeAgo(formatTimeAgo(createdAt))
                .build();
    }

    private static String formatTimeAgo(LocalDateTime createdAt) {
        long minutes = java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        long hours = minutes / 60;
        if (hours < 24) return hours + "시간 전";
        long days = hours / 24;
        return days + "일 전";
    }
}
