package com.example.peopoolbe.global.sse.service;

import com.example.peopoolbe.global.sse.domain.ActionType;
import com.example.peopoolbe.global.sse.domain.EventType;
import com.example.peopoolbe.global.sse.domain.Notification;
import com.example.peopoolbe.global.sse.domain.repository.NotificationRepository;
import com.example.peopoolbe.global.sse.dto.NotificationRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterManager sseEmitterManager;

    @Transactional
    public void notifyUser(Long receiverId, NotificationRes notificationRes, String eventType) {
        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .senderName(notificationRes.senderName())
                .eventType(notificationRes.eventType())
                .actionType(notificationRes.actionType())
                .targetId(notificationRes.targetId())
                .message(notificationRes.message())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        boolean sent = sseEmitterManager.sendToUser(receiverId, notification, eventType);
        if (!sent) {
            System.out.println("유저가 접속 중이 아님, DB에만 저장됨");
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationRes> getUnReadNotifications(Long receiverId) {
        return notificationRepository.findBySenderIdAndReadFalse(receiverId).stream()
                .map(n -> NotificationRes.of(
                        n.getEventType(),
                        n.getActionType(),
                        n.getTargetId(),
                        n.getSenderName(),
                        n.getMessage(),
                        n.getCreatedAt()
                ))
                .toList();
    }
}
