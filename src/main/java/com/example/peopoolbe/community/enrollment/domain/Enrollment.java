package com.example.peopoolbe.community.enrollment.domain;

import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Enrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private LocalDateTime decisionAt;

    @Builder
    public Enrollment(Member member, Post post, EnrollmentStatus status, LocalDateTime decisionAt) {
        this.member = member;
        this.post = post;
        this.status = status;
        this.decisionAt = decisionAt;
    }
}
