package com.example.peopoolbe.community.post.service;

import com.example.peopoolbe.community.post.api.dto.request.PostAddReq;
import com.example.peopoolbe.community.post.api.dto.request.PostUpdateReq;
import com.example.peopoolbe.community.post.api.dto.response.PostInfoRes;
import com.example.peopoolbe.community.post.api.dto.response.PostListRes;
import com.example.peopoolbe.community.post.domain.Category;
import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.community.post.domain.Status;
import com.example.peopoolbe.community.post.domain.repository.PostRepository;
import com.example.peopoolbe.global.s3.service.S3Service;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final S3Service s3Service;

    public PostInfoRes addPost(PostAddReq postAddReq, MultipartFile image, Principal principal) {
        Member member = memberService.getUserByToken(principal);

        checkDateStartEndOrder(postAddReq.recruitmentStartDate(), postAddReq.recruitmentEndDate());

        if(postAddReq.recruitmentStartDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("모집 시작 날짜가 현재 날짜보다 앞섭니다.");
        if(postAddReq.activityStartDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("활동 시작 날짜가 현재 날짜보다 앞섭니다.");

        Post post = Post.builder()
                .title(postAddReq.title())
                .content(postAddReq.content())
                .recruitmentStartDate(postAddReq.recruitmentStartDate())
                .recruitmentEndDate(postAddReq.recruitmentEndDate())
                .activityStartDate(postAddReq.activityStartDate())
                .maximumPeople(postAddReq.maxPeople())
                .status(postAddReq.recruitmentStartDate().isEqual(LocalDate.now()) ? Status.RECRUITING : Status.UPCOMING)
                .category(postAddReq.category())
                .image(s3Service.uploadNewPostImage(image))
                .member(member)
                .build();

        postRepository.save(post);

        return PostInfoRes.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .title(postAddReq.title())
                .content(postAddReq.content())
                .recruitmentStartDate(postAddReq.recruitmentStartDate())
                .recruitmentEndDate(postAddReq.recruitmentEndDate())
                .activityStartDate(postAddReq.activityStartDate())
                .maxPeople(postAddReq.maxPeople())
                .status(post.getStatus())
                .category(postAddReq.category())
                .image(post.getImage())
                .writerId(member.getId())
                .writerName(member.getNickname())
                .build();
    }

    public PostInfoRes getPostInfo(Long postId) {
        Post post = getPostByPostId(postId);

        return PostInfoRes.builder()
                .id(postId)
                .updatedAt(post.getUpdatedAt())
                .createdAt(post.getCreatedAt())
                .title(post.getTitle())
                .content(post.getContent())
                .recruitmentStartDate(post.getRecruitmentStartDate())
                .recruitmentEndDate(post.getRecruitmentEndDate())
                .activityStartDate(post.getActivityStartDate())
                .maxPeople(post.getMaximumPeople())
                .status(post.getStatus())
                .category(post.getCategory())
                .image(post.getImage())
                .writerId(post.getMember().getId())
                .writerName(post.getMember().getNickname())
                .build();
    }

    public PostListRes getPostList(String word, int page, int size, String startDate, String endDate, Category category, Status status) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("id").descending());
        Page<Post> postPage;

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        postPage = postRepository.searchPost(pageable, word, start, end, category, status);
        List<Post> postList = postPage.getContent();
        long totalCount = postPage.getTotalElements();
        int totalPages = postPage.getTotalPages();
        int currentPage = postPage.getNumber() + 1;

        List<PostInfoRes> postInfoResList = postList.stream()
                .map(PostInfoRes::from)
                .toList();

        return PostListRes.builder()
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .postList(postInfoResList)
                .build();
    }

//    public PostListRes searchPost(String word, int page, int size, String startDateTime, String endDateTime){
//        Pageable pageable = PageRequest.of(page-1, size);
//
//        LocalDateTime start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//        LocalDateTime end = LocalDateTime.parse(endDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//
//        Page<Post> postPage = postRepository.searchPost(pageable, word, start, end);
//
//        List<Post> postList = postPage.getContent();
//
//        List<PostInfoRes> postInfoResList = postList.stream()
//                .map(PostInfoRes::from)
//                .toList();
//
//        return PostListRes.fromPostList(postInfoResList);
//    }

    public PostInfoRes updatePost(Long postId, PostUpdateReq postUpdateReq, MultipartFile image, Principal principal) {
        Member member = memberService.getUserByToken(principal);
        Post post = getPostByPostId(postId);

        checkWriter(member, post);
        checkDateStartEndOrder(postUpdateReq.recruitmentStartDate(), postUpdateReq.recruitmentEndDate());

        if(!post.getRecruitmentStartDate().isEqual(postUpdateReq.recruitmentStartDate()))
            if(postUpdateReq.recruitmentStartDate().isBefore(LocalDate.now()))
                throw new IllegalArgumentException("수정된 모집 시작 날짜가 현재 날짜보다 앞섭니다.");
        if(!post.getActivityStartDate().isEqual(postUpdateReq.activityStartDate()))
            if(postUpdateReq.activityStartDate().isBefore(LocalDate.now()))
                throw new IllegalArgumentException("수정된 활동 시작 날짜가 현재 날짜보다 앞섭니다.");

        post.update(postUpdateReq.title(), postUpdateReq.content(), postUpdateReq.recruitmentStartDate(),
                postUpdateReq.recruitmentEndDate(), postUpdateReq.activityStartDate(), postUpdateReq.maxPeople(),
                postUpdateReq.status(), postUpdateReq.category(), s3Service.uploadExistingPostImage(image, postId));
        postRepository.save(post);

        return PostInfoRes.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .title(post.getTitle())
                .content(post.getContent())
                .recruitmentStartDate(post.getRecruitmentStartDate())
                .recruitmentEndDate(post.getRecruitmentEndDate())
                .activityStartDate(post.getActivityStartDate())
                .maxPeople(post.getMaximumPeople())
                .status(post.getStatus())
                .category(post.getCategory())
                .image(post.getImage())
                .writerId(member.getId())
                .writerName(member.getNickname())
                .build();
    }

    public void deletePost(Long postId, Principal principal) {
        Member member = memberService.getUserByToken(principal);
        Post post = getPostByPostId(postId);
        checkWriter(member, post);

        postRepository.delete(post);
    }

    private Post getPostByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시물"));
    }

    private void checkWriter(Member member, Post post) {
        if(!post.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("접근 권한 없음");
        }
    }

    private void checkDateStartEndOrder(LocalDate startDate, LocalDate endDate) {
        if(startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("날짜의 순서가 올바르지 않음");
        }
    }
}
