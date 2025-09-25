package com.example.peopoolbe.chat.service;

import com.example.peopoolbe.chat.api.dto.req.MessageSendReq;
import com.example.peopoolbe.global.sse.domain.ActionType;
import com.example.peopoolbe.global.sse.domain.EventType;
import com.example.peopoolbe.global.sse.dto.NotificationRes;
import com.example.peopoolbe.chat.api.dto.res.ChatRes;
import com.example.peopoolbe.chat.api.dto.res.ChatRoomInfo;
import com.example.peopoolbe.chat.api.dto.res.ChatRoomListRes;
import com.example.peopoolbe.chat.domain.Chat;
import com.example.peopoolbe.chat.domain.ChatRoom;
import com.example.peopoolbe.chat.domain.repository.ChatRepository;
import com.example.peopoolbe.chat.domain.repository.ChatRoomRepository;
import com.example.peopoolbe.global.sse.service.NotificationService;
import com.example.peopoolbe.global.sse.service.SseEmitterManager;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.repository.MemberRepository;
import com.example.peopoolbe.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    @Transactional
    public ChatRes sendMessage(Principal principal, MessageSendReq messageSendReq) {
        Member sender = memberService.getUserByToken(principal);
        Member receiver = memberRepository.findById(messageSendReq.receiverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 멤버입니다."));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("본인에게는 메시지를 보낼 수 없습니다.");
        }
        if (!StringUtils.hasText(messageSendReq.message())) {
            throw new IllegalArgumentException("메시지는 비어있을 수 없습니다.");
        }

        Member member1 = sender.getId() < messageSendReq.receiverId() ? sender : receiver;
        Member member2 = sender.getId() < messageSendReq.receiverId() ? receiver : sender;

        ChatRoom chatRoom = chatRoomRepository.findByMember1AndMember2(member1, member2)
                .orElseGet(() -> createRoom(member1, member2));

        Chat chat = Chat.builder()
                .chatroom(chatRoom)
                .sender(sender)
                .receiver(receiver)
                .message(messageSendReq.message())
                .build();
        Chat saved = chatRepository.save(chat);

        chatRoom.updateLastMessage(messageSendReq.message());

        NotificationRes notification = NotificationRes.builder()
                .eventType(EventType.CHAT)
                .actionType(ActionType.NEW_MESSAGE)
                .targetId(chatRoom.getId())
                .senderName(sender.getNickname())
                .message(messageSendReq.message())
                .build();
        notificationService.notifyUser(receiver.getId(), notification, "chat");

        return ChatRes.from(saved, sender.getId());
    }

    @Transactional(readOnly = true)
    public ChatRoomListRes getMyChatRooms(Principal principal) {
        Member member = memberService.getUserByToken(principal);
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByMember1OrMember2OrderByLastMessageTimeDesc(member, member);

        List<ChatRoomInfo> chatRoomInfos = chatRooms.stream()
                .map(chatRoom -> {
                    long unread = chatRepository.countByChatroomAndReceiverAndIsReadFalse(chatRoom, member);
                    return ChatRoomInfo.from(member, chatRoom, unread);
                })
                .toList();

        return ChatRoomListRes.from(chatRoomInfos);
    }

    @Transactional
    public List<ChatRes> getMessages(Principal principal, Long roomId) {
        Member member = memberService.getUserByToken(principal);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        int updatedCount = chatRepository.markAsRead(chatRoom, member);

        if (updatedCount > 0) {
            notificationService.markChatNotificationsAsRead(member.getId(), chatRoom.getId());

            NotificationRes readNotification = NotificationRes.builder()
                    .eventType(EventType.CHAT)
                    .actionType(ActionType.READ_MESSAGE)
                    .targetId(chatRoom.getId())
                    .senderName(member.getNickname())
                    .message("상대방이 메시지를 읽었습니다.")
                    .build();

            Member opponent = chatRoom.getMember1().equals(member) ? chatRoom.getMember2() : chatRoom.getMember1();
            notificationService.notifyUser(opponent.getId(), readNotification, "chat");
        }
        return chatRepository.findByChatroomOrderByCreatedAtAsc(chatRoom)
                .stream()
                .map(chat -> (ChatRes.from(chat, member.getId())))
                .toList();
    }

    private ChatRoom createRoom(Member member1, Member member2) {
        try {
            ChatRoom newChatRoom = ChatRoom.builder()
                    .member1(member1)
                    .member2(member2)
                    .build();
            return chatRoomRepository.save(newChatRoom);
        } catch (DataIntegrityViolationException e) {
            return chatRoomRepository.findByMember1AndMember2(member1, member2)
                    .orElseThrow(() -> e);
        }
    }
}
