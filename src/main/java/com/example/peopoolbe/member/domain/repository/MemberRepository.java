package com.example.peopoolbe.member.domain.repository;

import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.ViewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    List<Member> findMemberByProfileVisible (ViewStatus viewStatus);
}
