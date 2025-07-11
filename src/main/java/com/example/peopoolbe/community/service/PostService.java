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
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
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
                .endDate(postAddReq.endDate())
                .maxPeople(postAddReq.maxPeople())
                .status(Status.RECRUITING)
                .writerName(member.getNickname())
                .build();
    }

    public PostInfoRes getPostInfo(Long postId, Principal principal) {
        Member member = memberService.getUserByToken(principal);
        Post post = getPostByPostId(postId);

        checkWriter(member, post);

        return PostInfoRes.builder()
                .id(postId)
                .title(post.getTitle())
                .content(post.getTitle())
                .endDate(post.getRecruitmentEndDate())
                .maxPeople(post.getMaximumPeople())
                .status(post.getStatus())
                .writerName(post.getMember().getNickname())
                .build();
    }

    public PostListRes getPostList() {
        List<Post> postList = postRepository.findAll();

        List<PostInfoRes> postInfoResList = postList.stream()
                .map(PostInfoRes::from)
                .toList();

        return PostListRes.fromPostList(postInfoResList);
    }

    public PostListRes searchPost(String word) {
        List<Post> postList = new ArrayList<>();
        postList.addAll(searchPostByContent(word));
        postList.addAll(searchPostByTitle(word));
        postList.addAll(searchPostByWriterName(word));

        HashSet<Post> postHashSet = new HashSet<>(postList);
        postList.clear();
        postList.addAll(postHashSet);
        postList.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));

        List<PostInfoRes> postInfoResList = postList.stream()
                .map(PostInfoRes::from)
                .toList();

        return PostListRes.fromPostList(postInfoResList);
    }

    private List<Post> searchPostByTitle(String title) {
        return postRepository.findByTitleContainingOrderById(title);
    }

    private List<Post> searchPostByContent(String content) {
        return postRepository.findByContentContainingOrderById(content);
    }

    private List<Post> searchPostByWriterName(String writerName) {
        return postRepository.findByMember_NicknameContainingOrderById(writerName);
    }

    public PostInfoRes updatePost(Long postId, PostUpdateReq postUpdateReq, Principal principal) {
        Member member = memberService.getUserByToken(principal);
        Post post = getPostByPostId(postId);

        checkWriter(member, post);

        post.update(postUpdateReq.title(), postUpdateReq.content(), postUpdateReq.endDate(), postUpdateReq.maxPeople(), postUpdateReq.status());
        postRepository.save(post);

        return PostInfoRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
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
