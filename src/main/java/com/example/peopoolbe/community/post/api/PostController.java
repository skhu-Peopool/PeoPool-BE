package com.example.peopoolbe.community.post.api;

import com.example.peopoolbe.community.post.api.dto.request.PostAddReq;
import com.example.peopoolbe.community.post.api.dto.request.PostUpdateReq;
import com.example.peopoolbe.community.post.api.dto.response.PostInfoRes;
import com.example.peopoolbe.community.post.api.dto.response.PostListRes;
import com.example.peopoolbe.community.post.domain.Category;
import com.example.peopoolbe.community.post.domain.PostStatus;
import com.example.peopoolbe.community.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "게시물 등록", description = "커뮤니티에 게시물을 등록")
    @RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = PostAddDoc.class)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시물 등록 성공"),
            @ApiResponse(responseCode = "400", description = "게시물 등록 에러"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "뭔가 하나 값이 빠짐")
    })
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostInfoRes> addPost(Principal principal,
                                               @Parameter(hidden = true) @RequestPart("postAddReq") String postAddReqJson,
                                               @Parameter(hidden = true) @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        PostAddReq postAddReq = objectMapper.readValue(postAddReqJson, PostAddReq.class);
        return postService.addPost(postAddReq, image, principal);
    }

    @Operation(summary = "게시물 조회", description = "게시물 ID값으로 게시물을 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoRes> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostInfo(postId));
    }

//    @Operation(summary = "게시물 리스트 조회", description = "게시물 리스트를 조회, 토큰이 불필요, 페이지는 1부터 시작")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "조회 성공")
//    })
//    @GetMapping("/list")
//    public ResponseEntity<PostListRes> getPostList(@RequestParam(defaultValue = "1") int page,
//                                                   @RequestParam(defaultValue = "6") int size) {
//        return ResponseEntity.ok(postService.getPostList(page, size));
//    }

    @Operation(summary = "게시물 리스트 조회", description = "게시물 리스트를 조회, 검색어와 마감날짜를 쿼리스트링으로 입력하여 검색 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "검색어를 안넣었거나, 기타 오류 발생")
    })
    @GetMapping("/list")
    public ResponseEntity<PostListRes> searchPost(@RequestParam(defaultValue = "") String query,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "6") int size,
                                                  @RequestParam(defaultValue = "1900-01-01") String start,
                                                  @RequestParam(defaultValue = "2100-01-01") String end,
                                                  @RequestParam(defaultValue = "") Category category,
                                                  @RequestParam(defaultValue = "") PostStatus postStatus) {
        return ResponseEntity.ok(postService.getPostList(query, page, size, start, end, category, postStatus));
    }

    @Operation(summary = "게시물 수정", description = "본인이 작성한 게시물 수정")
    @RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = PostUpdateDoc.class)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 수정 성공"),
            @ApiResponse(responseCode = "400", description = "게시물 수정 에러"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "수정 권한 없음")
    })
    @PatchMapping(value = "/update/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostInfoRes> updatePost(Principal principal, @PathVariable Long postId,
                                                  @Parameter(hidden = true) @RequestPart("postUpdateReq") String postUpdateReqJson,
                                                  @Parameter(hidden = true) @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        PostUpdateReq postUpdateReq = objectMapper.readValue(postUpdateReqJson, PostUpdateReq.class);
        return postService.updatePost(postId, postUpdateReq, image, principal);
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
    
    class PostAddDoc {
        @Schema(required = true, description = "게시글 JSON 본문")
        public PostAddReq postAddReq;

        @Schema(type = "string", format = "binary", description = "이미지 파일(선택)")
        public MultipartFile image;
    }
    
    class PostUpdateDoc{
        @Schema(required = true, description = "수정 JSON 본문")
        public PostUpdateReq postUpdateReq;

        @Schema(type = "string", format = "binary", description = "이미지 파일(선택)")
        public MultipartFile image;
    }
}
