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
        LocalDateTime createdAt
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
                .build();
    }
}
