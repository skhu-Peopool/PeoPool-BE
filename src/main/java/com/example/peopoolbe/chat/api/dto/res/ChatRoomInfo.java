package com.example.peopoolbe.chat.api.dto.res;

import com.example.peopoolbe.chat.domain.ChatRoom;
import com.example.peopoolbe.member.domain.Member;
import lombok.Builder;

@Builder
public record ChatRoomInfo(
        Long roomId,
        Long opponentId,
        String opponentName,
        String opponentImage,
        String lastMessage,
        String lastMessageTime,
        Long unreadCount
) {
    public static ChatRoomInfo from(Member member, ChatRoom chatRoom, Long unreadCount) {
        Member opponent = chatRoom.getMember1().equals(member) ? chatRoom.getMember2() : chatRoom.getMember1();
        return ChatRoomInfo.builder()
                .roomId(chatRoom.getId())
                .opponentId(opponent.getId())
                .opponentName(opponent.getNickname())
                .opponentImage(opponent.getProfileImage() == null ? null : opponent.getProfileImage().getPath())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime() == null ? null : chatRoom.getLastMessageTime().toString())
                .unreadCount(unreadCount)
                .build();
    }
}
