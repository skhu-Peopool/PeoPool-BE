package com.example.peopoolbe.global.sse.dto;

import com.example.peopoolbe.global.sse.domain.ActionType;
import com.example.peopoolbe.global.sse.domain.EventType;
import lombok.Builder;

@Builder
public record NotificationRes(
        EventType eventType,
        ActionType actionType,
        Long targetId,
        String senderName,
        String message
) {
}
