package com.example.peopoolbe.global.sse.domain.repository;

import com.example.peopoolbe.global.sse.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdAndIsReadFalse(Long receiverId);
}
