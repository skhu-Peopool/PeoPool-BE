package com.example.peopoolbe.chat.api;

import com.example.peopoolbe.chat.api.dto.req.MessageSendReq;
import com.example.peopoolbe.chat.api.dto.res.ChatRes;
import com.example.peopoolbe.chat.api.dto.res.ChatRoomListRes;
import com.example.peopoolbe.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅 전송", description = "상대방의 id값으로 상대를 추적하여 메시지 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메시지 전송 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/send")
    public ResponseEntity<ChatRes> sendMessage(Principal principal, @RequestBody MessageSendReq messageSendReq) {
        return ResponseEntity.ok(chatService.sendMessage(principal, messageSendReq));
    }

    @Operation(summary = "내 채팅방 리스트 확인", description = "토큰 사용자 본인의 채팅방 목록을 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 리스트 확인"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/my")
    public ResponseEntity<ChatRoomListRes> getMyChatRooms(Principal principal) {
        return ResponseEntity.ok(chatService.getMyChatRooms(principal));
    }

    @Operation(summary = "채팅방 접속", description = "해당 채팅방에 접속하여 채팅 내역 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 접속"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/chatroom/{roomId}")
    public ResponseEntity<List<ChatRes>> getMessages(Principal principal, @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getMessages(principal, roomId));
    }
}
