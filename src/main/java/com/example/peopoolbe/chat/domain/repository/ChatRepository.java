package com.example.peopoolbe.chat.domain.repository;

import com.example.peopoolbe.chat.domain.Chat;
import com.example.peopoolbe.chat.domain.ChatRoom;
import com.example.peopoolbe.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByChatroomOrderByCreatedAtAsc(ChatRoom chatRoom);

    long countByChatroomAndReceiverAndIsReadFalse(ChatRoom chatRoom, Member receiver);

    @Modifying
    @Query("UPDATE Chat c " +
            "SET c.isRead = true, c.readAt = CURRENT_TIMESTAMP " +
            "WHERE c.chatroom = :chatRoom " +
            "AND c.receiver = :receiver " +
            "AND c.isRead = false")
    int markAsRead(ChatRoom chatRoom, Member receiver);
}
