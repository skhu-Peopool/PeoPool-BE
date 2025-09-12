package com.example.peopoolbe.community.enrollment.domain.repository;

import com.example.peopoolbe.community.enrollment.domain.Enrollment;
import com.example.peopoolbe.community.enrollment.domain.EnrollmentStatus;
import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByMemberAndPostAndStatusIsNot(Member member, Post post, EnrollmentStatus status);

    Integer countByPostIdAndStatusIs(Long postId, EnrollmentStatus status);

    Optional<Enrollment> findByMemberAndPost(Member member, Post post);

    @Query("""
        SELECT count(DISTINCT e.member) FROM Enrollment e
        WHERE e.post.id = :postId
    """)
    Integer countEnrollmentByPostId(Long postId);
}
