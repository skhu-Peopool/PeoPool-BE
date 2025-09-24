package com.example.peopoolbe.global.sse.controller;

import com.example.peopoolbe.global.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final SseEmitterManager sseEmitterManager;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam Long memberId) {
        return sseEmitterManager.createSseEmitter(memberId);
    }
}
