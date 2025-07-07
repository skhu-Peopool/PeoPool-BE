package com.example.peopoolbe.community.api;

import com.example.peopoolbe.community.api.dto.request.PostAddReq;
import com.example.peopoolbe.community.api.dto.request.PostUpdateReq;
import com.example.peopoolbe.community.api.dto.response.PostInfoRes;
import com.example.peopoolbe.community.api.dto.response.PostListRes;
import com.example.peopoolbe.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/add")
    public ResponseEntity<PostInfoRes> addPost(@RequestBody PostAddReq postAddReq, Principal principal) {
        return ResponseEntity.ok(postService.addPost(postAddReq, principal));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoRes> getPost(@PathVariable Long postId, Principal principal) {
        return ResponseEntity.ok(postService.getPostInfo(postId, principal));
    }

    @GetMapping("/list")
    public ResponseEntity<PostListRes> getPostList() {
        return ResponseEntity.ok(postService.getPostList());
    }

    @GetMapping("/search")
    public ResponseEntity<PostListRes> searchPost(@RequestParam String query) {
        return ResponseEntity.ok(postService.searchPost(query));
    }

    @PatchMapping("/update/{postId}")
    public ResponseEntity<PostInfoRes> updatePost(@PathVariable Long postId, @RequestBody PostUpdateReq postUpdateReq, Principal principal) {
        return ResponseEntity.ok(postService.updatePost(postId, postUpdateReq, principal));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Principal principal) {
        postService.deletePost(postId, principal);

        return ResponseEntity.ok().build();
    }
}
