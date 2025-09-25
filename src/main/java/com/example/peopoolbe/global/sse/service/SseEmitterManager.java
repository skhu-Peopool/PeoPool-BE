package com.example.peopoolbe.global.sse.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterManager {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createSseEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        return emitter;
    }

    public boolean sendToUser(Long memberId, Object data, String eventType) {
        SseEmitter emitter = emitters.get(memberId);
        System.out.println("sendToUser: " + memberId + ", data: " + data + ", emitter: " + emitter);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventType).data(data));
                return true;
            } catch (Exception e) {
                emitters.remove(memberId);
                return false;
            }
        }
        return false;
    }

    @Scheduled(fixedRate = 25_000)
    public void sendKeepAlive() {
        emitters.forEach((memberId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("ping")
                        .data("keep-alive"));
            } catch (Exception e) {
                emitters.remove(memberId);
            }
        });
    }
}
