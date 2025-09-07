package com.example.peopoolbe.community.enrollment.service;

import com.example.peopoolbe.community.enrollment.api.dto.EnrollmentApplyingList;
import com.example.peopoolbe.community.enrollment.api.dto.EnrollmentApplyingRes;
import com.example.peopoolbe.community.enrollment.domain.Enrollment;
import com.example.peopoolbe.community.enrollment.domain.EnrollmentStatus;
import com.example.peopoolbe.community.enrollment.domain.repository.EnrollmentRepository;
import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.community.post.domain.repository.PostRepository;
import com.example.peopoolbe.community.post.service.PostService;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final MemberService memberService;
    private final PostRepository postRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PostService postService;

    public EnrollmentApplyingRes applyEnrollment(Principal principal, Long postId) {
        Member member = memberService.getUserByToken(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if(enrollmentRepository.existsByMemberAndPost(member, post))
            throw new IllegalArgumentException("이미 신청된 목록입니다.");

        Enrollment enrollment = Enrollment.builder()
                .member(member)
                .post(post)
                .status(EnrollmentStatus.PENDING)
                .build();
        enrollmentRepository.save(enrollment);

        return EnrollmentApplyingRes.builder()
                .memberId(member.getId())
                .postId(postId)
                .appliedAt(enrollment.getCreatedAt())
                .build();
    }

    public EnrollmentApplyingList getApplyingListByMember(Principal principal) {
        Member member = memberService.getUserByToken(principal);
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        List<EnrollmentApplyingRes> enrollmentApplyingList = enrollments.stream()
                .filter(enrollment -> Objects.equals(enrollment.getMember().getId(), member.getId()))
                .map(EnrollmentApplyingRes::from)
                .toList();

        return EnrollmentApplyingList.fromApplyingRes(enrollmentApplyingList);
    }

    public EnrollmentApplyingList getApplyingListByPost(Principal principal, Long postId) {
        Member member = memberService.getUserByToken(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        postService.checkWriter(member, post);

        List<Enrollment> enrollments = enrollmentRepository.findAll();

        List<EnrollmentApplyingRes> enrollmentApplyingList = enrollments.stream()
                .filter(enrollment -> Objects.equals(enrollment.getPost().getId(), postId))
                .map(EnrollmentApplyingRes::from)
                .toList();

        return EnrollmentApplyingList.fromApplyingRes(enrollmentApplyingList);
    }

    public EnrollmentApplyingList getApplyingList() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        List<EnrollmentApplyingRes> enrollmentApplyingList = enrollments.stream()
                .map(EnrollmentApplyingRes::from)
                .toList();
        return EnrollmentApplyingList.fromApplyingRes(enrollmentApplyingList);
    }

    public void cancelEnrollment(Principal principal, Long postId) {
        Member member = memberService.getUserByToken(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Enrollment enrollment = enrollmentRepository.findByMemberAndPost(member, post)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 내역입니다."));
        enrollmentRepository.delete(enrollment);
    }
}
