package com.example.peopoolbe.chat.domain;

import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Chat extends BaseEntity {

    @Column(nullable = false, length = 1000)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatroom;

    private boolean isRead = false;

    private LocalDateTime readAt;

    @Builder
    public Chat(String message, Member sender, Member receiver, ChatRoom chatroom, boolean isRead, LocalDateTime readAt) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.chatroom = chatroom;
        this.isRead = isRead;
        this.readAt = readAt;
    }
}
