package com.example.peopoolbe.community.enrollment.domain.repository;

import com.example.peopoolbe.community.enrollment.domain.Enrollment;
import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByMemberAndPost(Member member, Post post);

    Optional<Enrollment> findByMemberAndPost(Member member, Post post);
}
