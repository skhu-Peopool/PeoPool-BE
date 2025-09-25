package com.example.peopoolbe.global.sse.controller;

import com.example.peopoolbe.global.sse.dto.NotificationRes;
import com.example.peopoolbe.global.sse.service.NotificationService;
import com.example.peopoolbe.global.sse.service.SseEmitterManager;
import com.example.peopoolbe.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final SseEmitterManager sseEmitterManager;
    private final NotificationService notificationService;

    @Operation(summary = "SSE를 활용한 알림 구독", description = "실시간 알림 수신")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SSE 연결 성공, 이후 이벤트 전송(재연결 필요 X)"),
            @ApiResponse(responseCode = "403", description = "토큰 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        SseEmitter sseEmitter = sseEmitterManager.createSseEmitter(memberId);

        CompletableFuture.runAsync(() -> {
            List<NotificationRes> unReadList = notificationService.getUnReadNotifications(memberId);
            if (!unReadList.isEmpty()) {
                sseEmitterManager.sendToUser(memberId, unReadList, "initial-notifications");
            }
        });

        return sseEmitter;
    }
}
