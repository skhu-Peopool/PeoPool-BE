package com.example.peopoolbe.chat.api.dto.req;

public record MessageSendReq(
        Long receiverId,
        String message
) {
}
