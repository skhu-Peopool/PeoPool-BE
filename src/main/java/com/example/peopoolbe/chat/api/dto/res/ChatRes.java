package com.example.peopoolbe.chat.api.dto.res;

import com.example.peopoolbe.chat.domain.Chat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatRes(
        Long messageId,
        Long senderId,
        Long receiverId,
        String message,
        boolean isRead,
        boolean isMine,
        LocalDateTime createdAt,
        String readTimeAgo
) {
    public static ChatRes from(Chat chat, Long memberId) {
        return ChatRes.builder()
                .messageId(chat.getId())
                .senderId(chat.getSender().getId())
                .receiverId(chat.getReceiver().getId())
                .message(chat.getMessage())
                .isRead(chat.isRead())
                .isMine(chat.getSender().getId().equals(memberId))
                .createdAt(chat.getCreatedAt())
                .readTimeAgo(formatTime(chat.getReadAt()))
                .build();
    }

    public static String formatTime(LocalDateTime readAt) {
        if (readAt == null) return null;

        long minutes = java.time.Duration.between(readAt, LocalDateTime.now()).toMinutes();
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        long hours = minutes / 60;
        if (hours < 24) return hours + "시간 전";
        long days = hours / 24;
        return days + "일 전";

    }
}
