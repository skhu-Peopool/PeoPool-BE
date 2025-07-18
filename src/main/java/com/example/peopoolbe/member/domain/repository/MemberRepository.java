package com.example.peopoolbe.member.domain.repository;

import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.ProfileVisible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String id);

    List<Member> findMemberByProfileVisible (ProfileVisible profileVisible);
}
