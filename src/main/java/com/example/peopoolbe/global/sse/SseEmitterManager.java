package com.example.peopoolbe.global.sse;

import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.service.MemberService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterManager {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final MemberService memberService;

    public SseEmitterManager(MemberService memberService) {
        this.memberService = memberService;
    }

    public SseEmitter createSseEmitter(Principal principal) {
        Member member = memberService.getUserByToken(principal);
        Long memberId = member.getId();
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        return emitter;
    }

    public void sendToUser(Long memberId, Object data) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("chat").data(data));
            } catch (Exception e) {
                emitters.remove(memberId);
            }
        }
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
