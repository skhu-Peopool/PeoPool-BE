package com.example.peopoolbe.chat.api.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatRoomListRes(
        List<ChatRoomInfo> chatRoomList
) {
    public static ChatRoomListRes from(List<ChatRoomInfo> chatRooms) {
        return ChatRoomListRes.builder()
                .chatRoomList(chatRooms)
                .build();
    }
}
