package com.example.peopoolbe.chat.domain.repository;

import com.example.peopoolbe.chat.domain.ChatRoom;
import com.example.peopoolbe.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByMember1AndMember2(Member member1, Member member2);

    List<ChatRoom> findAllByMember1OrMember2OrderByLastMessageTimeDesc(Member member1, Member member2);
}
