package com.example.peopoolbe.community.service;

import com.example.peopoolbe.community.api.dto.request.PostAddReq;
import com.example.peopoolbe.community.api.dto.request.PostUpdateReq;
import com.example.peopoolbe.community.api.dto.response.PostInfoRes;
import com.example.peopoolbe.community.api.dto.response.PostListRes;
import com.example.peopoolbe.community.domain.Post;
import com.example.peopoolbe.community.domain.Status;
import com.example.peopoolbe.community.domain.repository.PostRepository;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;

    public PostInfoRes addPost(PostAddReq postAddReq, Principal principal) {
        Member member = memberService.getUserByToken(principal);

        Post post = Post.builder()
                .title(postAddReq.title())
                .content(postAddReq.content())
                .recruitmentStartDate(postAddReq.startDate())
                .recruitmentEndDate(postAddReq.endDate())
                .maximumPeople(postAddReq.maxPeople())
                .status(Status.RECRUITING)
                .member(member)
                .build();

        postRepository.save(post);

        return PostInfoRes.builder()
                .id(post.getId())
                .title(postAddReq.title())
                .content(postAddReq.content())
                .startDate(postAddReq.startDate())
                .endDate(postAddReq.endDate())
                .maxPeople(postAddReq.maxPeople())
                .status(Status.RECRUITING)
                .writerName(member.getNickname())
                .build();
    }

    public PostInfoRes getPostInfo(Long postId) {
        Post post = getPostByPostId(postId);

        return PostInfoRes.builder()
                .id(postId)
                .title(post.getTitle())
                .content(post.getTitle())
                .startDate(post.getRecruitmentStartDate())
                .endDate(post.getRecruitmentEndDate())
                .maxPeople(post.getMaximumPeople())
                .status(post.getStatus())
                .writerName(post.getMember().getNickname())
                .build();
    }

    public PostListRes getPostList(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Post> postPage = postRepository.findAll(pageable);

        List<Post> postList = postPage.getContent();

        List<PostInfoRes> postInfoResList = postList.stream()
                .map(PostInfoRes::from)
                .toList();

        return PostListRes.fromPostList(postInfoResList);
    }

    public PostListRes searchPost(String word, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Post> postPage = postRepository.findByTitleContainingOrContentContainingOrMember_NicknameContainingOrderById(pageable, word, word, word);

        List<Post> postList = postPage.getContent();

        List<PostInfoRes> postInfoResList = postList.stream()
                .map(PostInfoRes::from)
                .toList();

        return PostListRes.fromPostList(postInfoResList);
    }

    public PostInfoRes updatePost(Long postId, PostUpdateReq postUpdateReq, Principal principal) {
        Member member = memberService.getUserByToken(principal);
        Post post = getPostByPostId(postId);

        checkWriter(member, post);

        post.update(postUpdateReq.title(), postUpdateReq.content(), postUpdateReq.startDate() ,postUpdateReq.endDate(), postUpdateReq.maxPeople(), postUpdateReq.status());
        postRepository.save(post);

        return PostInfoRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .startDate(post.getRecruitmentStartDate())
                .endDate(post.getRecruitmentEndDate())
                .maxPeople(post.getMaximumPeople())
                .status(post.getStatus())
                .writerName(post.getMember().getNickname())
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
}
