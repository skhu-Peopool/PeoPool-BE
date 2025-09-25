package com.example.peopoolbe.global.sse.domain;

import com.example.peopoolbe.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Notification extends BaseEntity {

    private Long receiverId;

    private Long senderId;
    private String senderName;

    private String message;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private Long targetId;
    private boolean isRead;

    @Builder
    public Notification(Long receiverId, Long senderId, String senderName, String message, EventType eventType, ActionType actionType, Long targetId, boolean isRead) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.eventType = eventType;
        this.actionType = actionType;
        this.targetId = targetId;
        this.isRead = isRead;
    }
}
