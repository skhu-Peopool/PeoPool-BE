package com.example.peopoolbe.member.domain.repository;

import com.example.peopoolbe.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
