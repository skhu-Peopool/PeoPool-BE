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
        LocalDateTime createdAt
) {
    public static ChatRes from(Chat chat) {
        return ChatRes.builder()
                .messageId(chat.getId())
                .senderId(chat.getSender().getId())
                .receiverId(chat.getReceiver().getId())
                .message(chat.getMessage())
                .isRead(chat.isRead())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
