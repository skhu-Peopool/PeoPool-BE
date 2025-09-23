package com.example.peopoolbe.chat.domain;

import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"member1_id", "member2_id"}
        )
)
@Getter
@NoArgsConstructor
public class ChatRoom extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member1_id", nullable = false)
    private Member member1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member2_id", nullable = false)
    private Member member2;

    private String lastMessage;
    private LocalDateTime lastMessageTime;

    @Builder
    public ChatRoom(Member member1, Member member2, String lastMessage, LocalDateTime lastMessageTime) {
        this.member1 = member1;
        this.member2 = member2;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public void updateLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        this.lastMessageTime = LocalDateTime.now();
    }
}
