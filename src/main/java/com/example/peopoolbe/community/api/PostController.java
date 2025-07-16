package com.example.peopoolbe.community.api;

import com.example.peopoolbe.community.api.dto.request.PostAddReq;
import com.example.peopoolbe.community.api.dto.request.PostUpdateReq;
import com.example.peopoolbe.community.api.dto.response.PostInfoRes;
import com.example.peopoolbe.community.api.dto.response.PostListRes;
import com.example.peopoolbe.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 등록", description = "커뮤니티에 게시물을 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시물 등록 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음")
    })
    @PostMapping("/add")
    public ResponseEntity<PostInfoRes> addPost(Principal principal, @RequestBody PostAddReq postAddReq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.addPost(postAddReq, principal));
    }

    @Operation(summary = "게시물 조회", description = "게시물 ID값으로 게시물을 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoRes> getPost(Principal principal, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostInfo(postId, principal));
    }

    @Operation(summary = "게시물 리스트 조회", description = "게시물 리스트를 조회, 토큰이 불필요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/list")
    public ResponseEntity<PostListRes> getPostList(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(postService.getPostList(page, size));
    }

    @Operation(summary = "게시물 검색", description = "제목, 본문, 작성자를 검색하여 게시물 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음")
    })
    @GetMapping("/search")
    public ResponseEntity<PostListRes> searchPost(@RequestParam String query,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(postService.searchPost(query, page, size));
    }

    @Operation(summary = "게시물 수정", description = "본인이 작성한 게시물 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "수정 권한 없음")
    })
    @PatchMapping("/update/{postId}")
    public ResponseEntity<PostInfoRes> updatePost(Principal principal, @PathVariable Long postId, @RequestBody PostUpdateReq postUpdateReq) {
        return ResponseEntity.ok(postService.updatePost(postId, postUpdateReq, principal));
    }

    @Operation(summary = "게시물 삭제", description = "본인이 작성한 게시물 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "삭제 권한 없음")
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(Principal principal, @PathVariable Long postId) {
        postService.deletePost(postId, principal);

        return ResponseEntity.ok().build();
    }
}
