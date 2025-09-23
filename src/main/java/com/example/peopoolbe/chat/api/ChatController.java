package com.example.peopoolbe.chat.api;

import com.example.peopoolbe.chat.api.dto.req.MessageSendReq;
import com.example.peopoolbe.chat.api.dto.res.ChatRes;
import com.example.peopoolbe.chat.api.dto.res.ChatRoomListRes;
import com.example.peopoolbe.chat.service.ChatService;
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

    @PostMapping("/send")
    public ResponseEntity<ChatRes> sendMessage(Principal principal, @RequestBody MessageSendReq messageSendReq) {
        return ResponseEntity.ok(chatService.sendMessage(principal, messageSendReq));
    }

    @GetMapping("/my")
    public ResponseEntity<ChatRoomListRes> getMyChatRooms(Principal principal) {
        return ResponseEntity.ok(chatService.getMyChatRooms(principal));
    }

    @GetMapping("/chatroom/{roomId}")
    public ResponseEntity<List<ChatRes>> getMessages(Principal principal, @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getMessages(principal, roomId));
    }
}
