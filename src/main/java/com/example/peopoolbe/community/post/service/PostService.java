package com.example.peopoolbe.community.post.service;

import com.example.peopoolbe.community.post.api.dto.request.PostAddReq;
import com.example.peopoolbe.community.post.api.dto.request.PostUpdateReq;
import com.example.peopoolbe.community.post.api.dto.response.PostInfoRes;
import com.example.peopoolbe.community.post.api.dto.response.PostListRes;
import com.example.peopoolbe.community.post.domain.Category;
import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.community.post.domain.PostStatus;
import com.example.peopoolbe.community.post.domain.repository.PostRepository;
import com.example.peopoolbe.global.s3.domain.Image;
import com.example.peopoolbe.global.s3.domain.repository.ImageRepository;
import com.example.peopoolbe.global.s3.service.S3Service;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final S3Service s3Service;
    private final ImageRepository imageRepository;

    @Transactional
    public ResponseEntity addPost(PostAddReq postAddReq, MultipartFile[] image, Principal principal) {
        Member member = memberService.getUserByToken(principal);

        if(image != null && image.length > 5) {
            return ResponseEntity.status(400).body("사진은 최대 5장까지 업로드 가능");
        }
        if(postAddReq.recruitmentStartDate().isAfter(postAddReq.recruitmentEndDate()))
            return ResponseEntity.status(400).body("날짜의 순서가 올바르지 않음");
        if(postAddReq.recruitmentStartDate().isBefore(LocalDate.now()))
            return ResponseEntity.status(400).body("모집 시작 날짜가 현재 날짜보다 앞섭니다.");
        if(postAddReq.activityStartDate().isBefore(LocalDate.now()))
            return ResponseEntity.status(400).body("활동 시작 날짜가 현재 날짜보다 앞섭니다.");
        if(!postAddReq.activityStartDate().isAfter(postAddReq.recruitmentEndDate()))
            return ResponseEntity.status(400).body("활동 시작 날짜가 모집 마감 날짜보다 앞섭니다.");

        Post post = Post.builder()
                .title(postAddReq.title())
                .content(postAddReq.content())
                .recruitmentStartDate(postAddReq.recruitmentStartDate())
                .recruitmentEndDate(postAddReq.recruitmentEndDate())
                .activityStartDate(postAddReq.activityStartDate())
                .maximumPeople(postAddReq.maxPeople())
                .approvedPeople(0) // 작성자의 기대 모집 인원은 0부터 시작임
                .appliedPeople(0)
                .postStatus(postAddReq.recruitmentStartDate().isEqual(LocalDate.now()) ? PostStatus.RECRUITING : PostStatus.UPCOMING)
                .category(postAddReq.category())
                .views(0)
                .member(member)
                .build();

        postRepository.save(post);

        if (image != null && image.length > 0) {
            post.updateImages(Arrays.stream(image)
                    .map(file -> s3Service.uploadPostImage(file, post))
                    .toList());
        }
        postRepository.save(post);

        log.info("add post success - postId: {}, title: {}", post.getId(), post.getTitle());

        return ResponseEntity.status(HttpStatus.CREATED).body(PostInfoRes.from(post));
    }

    @Transactional
    public PostInfoRes getPostInfo(Long postId) {
        Post post = getPostByPostId(postId);
        post.incrementViews();
        postRepository.save(post);

        return PostInfoRes.from(post);
    }

    @Transactional
    public PostListRes getPostList(String word, int page, int size, String startDate, String endDate, Category category, PostStatus postStatus) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("id").descending());
        Page<Post> postPage;

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        postPage = postRepository.searchPost(pageable, word, start, end, category, postStatus, LocalDate.now().minusDays(3));
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

    @Transactional
    public PostListRes getMyPost(Principal principal) {
        Member member = memberService.getUserByToken(principal);

        List<Post> posts = member.getPosts();

        log.info("get posts memberId: {}", member.getId());

        return PostListRes.builder()
                .totalCount(posts.size())
                .postList(posts.stream().map(PostInfoRes::from).toList())
                .build();
    }

    @Transactional
    public ResponseEntity updatePost(Long postId, PostUpdateReq postUpdateReq, MultipartFile[] image, Principal principal) {
        Member member = memberService.getUserByToken(principal);
        Post post = getPostByPostId(postId);

        checkWriter(member, post);
        if (postUpdateReq.recruitmentStartDate().isAfter(postUpdateReq.recruitmentEndDate())) {
            log.warn("Validation failed: recruitmentStartDate={} is after recruitmentEndDate={}", postUpdateReq.recruitmentStartDate(), postUpdateReq.recruitmentEndDate());
            return ResponseEntity.status(400).body("날짜의 순서가 올바르지 않음");
        }

        if (!post.getRecruitmentStartDate().isEqual(postUpdateReq.recruitmentStartDate())) {
            if (postUpdateReq.recruitmentStartDate().isBefore(LocalDate.now())) {
                log.warn("Validation failed: recruitmentStartDate={} is before today", postUpdateReq.recruitmentStartDate());
                return ResponseEntity.status(400).body("수정된 모집 시작 날짜가 현재 날짜보다 앞섭니다.");
            }
            if (postUpdateReq.recruitmentStartDate().isEqual(LocalDate.now()) && postUpdateReq.postStatus() == PostStatus.UPCOMING) {
                log.warn("Validation failed: recruitmentStartDate is today but postStatus=UPCOMING is not allowed");
                return ResponseEntity.status(400).body("모집 중에는 모집 예정으로 변경할 수 없습니다.");
            }
        }

        if (!post.getActivityStartDate().isEqual(postUpdateReq.activityStartDate())  && postUpdateReq.activityStartDate().isBefore(LocalDate.now())) {
            log.warn("Validation failed: activityStartDate={} is before today", postUpdateReq.activityStartDate());
            return ResponseEntity.status(400).body("수정된 활동 시작 날짜가 현재 날짜보다 앞섭니다.");
        }

        if (postUpdateReq.recruitmentStartDate().isAfter(LocalDate.now()) && postUpdateReq.postStatus() != PostStatus.UPCOMING) {
            log.warn("Validation failed: recruitmentStartDate={} is after now, but postStatus={}", postUpdateReq.recruitmentStartDate(), postUpdateReq.postStatus());
            return ResponseEntity.status(400).body("모집 시작일 전에는 모집 속성을 변경할 수 없습니다.");
        }

        if (LocalDate.now().isAfter(postUpdateReq.recruitmentEndDate()) && postUpdateReq.postStatus() != PostStatus.RECRUITED) {
            log.warn("Validation failed: today is after recruitmentEndDate={}, but postStatus={}", postUpdateReq.recruitmentEndDate(), postUpdateReq.postStatus());
            return ResponseEntity.status(400).body("모집 마감일 후에는 모집 속성을 변경할 수 없습니다.");
        }

        if (!LocalDate.now().isBefore(postUpdateReq.recruitmentStartDate())
                && !LocalDate.now().isAfter(postUpdateReq.recruitmentEndDate())
                && postUpdateReq.postStatus() != PostStatus.RECRUITING
                && postUpdateReq.postStatus() != PostStatus.UNDER_REVIEW) {
            log.warn("Validation failed: today is between recruitmentStartDate={} and recruitmentEndDate={}, but postStatus={}",
                    postUpdateReq.recruitmentStartDate(), postUpdateReq.recruitmentEndDate(), postUpdateReq.postStatus());
            return ResponseEntity.status(400).body("모집 중에는 모집 속성을 모집 중 또는 검토 중으로만 변경 가능합니다.");
        }

        if (!postUpdateReq.activityStartDate().isAfter(postUpdateReq.recruitmentEndDate())) {
            log.warn("Validation failed: activityStartDate={} is not after recruitmentEndDate={}", postUpdateReq.activityStartDate(), postUpdateReq.recruitmentEndDate());
            return ResponseEntity.status(400).body("활동 시작 날짜가 모집 날짜보다 앞섭니다.");
        }

        if (image != null && post.getImage().size() - postUpdateReq.deleteImgUrl().length + image.length > 5) {
            log.warn("Validation failed: image count exceeded, max=5. current={}, deleteRequest={}, newUpload={}",
                    post.getImage().size(), postUpdateReq.deleteImgUrl().length, image.length);
            return ResponseEntity.status(400).body("사진은 최대 5장까지 업로드 가능");
        }

        post.update(
                postUpdateReq.title(),
                postUpdateReq.content(),
                postUpdateReq.recruitmentStartDate(),
                postUpdateReq.recruitmentEndDate(),
                postUpdateReq.activityStartDate(),
                postUpdateReq.maxPeople(),
                postUpdateReq.postStatus(),
                postUpdateReq.category()
        );

        if (postUpdateReq.deleteImgUrl() != null && postUpdateReq.deleteImgUrl().length > 0) {
            for (String url : postUpdateReq.deleteImgUrl()) {
                imageRepository.deleteByPath(url);
            }
        }
        List<Image> imgList = imageRepository.findAllByPost(post);

        if (image == null || image.length == 0) {
            post.updateImages(imgList);
        } else {
            imgList.addAll(Arrays.stream(image).
                    map(file -> s3Service.uploadPostImage(file, post))
                    .toList());
            post.updateImages(imgList);
        }
        postRepository.save(post);
        log.info("update post success - postId: {}, title: {}", post.getId(), post.getTitle());

        return ResponseEntity.ok(PostInfoRes.from(post));
    }

    @Transactional
    public void deletePost(Long postId, Principal principal) {
        Member member = memberService.getUserByToken(principal);
        Post post = getPostByPostId(postId);
        checkWriter(member, post);

        log.info("delete post success - postId: {}", postId);

        postRepository.delete(post);
    }

    private Post getPostByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시물"));
    }

    public void checkWriter(Member member, Post post) {
        if (!post.getMember().getId().equals(member.getId())) {
            log.warn("is not writer memberId: {}, postId: {}", member.getId(), post.getId());
            throw new IllegalArgumentException("접근 권한 없음");
        }
    }
}
