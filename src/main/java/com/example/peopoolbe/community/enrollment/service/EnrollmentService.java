package com.example.peopoolbe.community.enrollment.service;

import com.example.peopoolbe.community.enrollment.api.dto.EnrollmentApplyingList;
import com.example.peopoolbe.community.enrollment.api.dto.EnrollmentApplyingRes;
import com.example.peopoolbe.community.enrollment.api.dto.req.EnrollmentApplyingReq;
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
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final MemberService memberService;
    private final PostRepository postRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PostService postService;

    public EnrollmentApplyingRes applyEnrollment(Principal principal, Long postId, EnrollmentApplyingReq enrollmentApplyingReq) {
        Member member = memberService.getUserByToken(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if(enrollmentRepository.existsByMemberAndPostAndStatusIsNot(member, post, EnrollmentStatus.REJECTED))
            throw new IllegalArgumentException("이미 신청된 목록입니다.");

        Enrollment enrollment = Enrollment.builder()
                .member(member)
                .post(post)
                .comment(enrollmentApplyingReq.comment())
                .status(EnrollmentStatus.PENDING)
                .build();
        enrollmentRepository.save(enrollment);

        post.updateAppliedPeople(enrollmentRepository.countEnrollmentByPostId(postId));
        postRepository.save(post);

        return EnrollmentApplyingRes.from(enrollment);
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

    public EnrollmentApplyingList getEnrollmentList() {
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

        post.updateAppliedPeople(enrollmentRepository.countEnrollmentByPostId(post.getId()));
        postRepository.save(post);
    }

    public EnrollmentApplyingRes approveEnrollment(Principal principal, Long EnrollmentId) {
        Member member = memberService.getUserByToken(principal);
        Enrollment enrollment = enrollmentRepository.findById(EnrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 내역입니다."));
        Post post = postRepository.findById(enrollment.getPost().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        postService.checkWriter(member, post);
        enrollment.update(EnrollmentStatus.APPROVED, LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        post.updateApprovedPeople(enrollmentRepository.countByPostIdAndStatusIs(post.getId(), EnrollmentStatus.APPROVED));
        postRepository.save(post);

        return EnrollmentApplyingRes.from(enrollment);
    }

    public EnrollmentApplyingRes rejectEnrollment(Principal principal, Long EnrollmentId) {
        Member member = memberService.getUserByToken(principal);
        Enrollment enrollment = enrollmentRepository.findById(EnrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 내역입니다."));
        Post post = postRepository.findById(enrollment.getPost().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        postService.checkWriter(member, post);
        enrollment.update(EnrollmentStatus.REJECTED, LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        return EnrollmentApplyingRes.from(enrollment);
    }
}
