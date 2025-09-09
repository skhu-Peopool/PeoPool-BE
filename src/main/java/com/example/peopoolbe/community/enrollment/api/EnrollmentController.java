package com.example.peopoolbe.community.enrollment.api;

import com.example.peopoolbe.community.enrollment.api.dto.EnrollmentApplyingList;
import com.example.peopoolbe.community.enrollment.api.dto.EnrollmentApplyingRes;
import com.example.peopoolbe.community.enrollment.api.dto.req.EnrollmentApplyingReq;
import com.example.peopoolbe.community.enrollment.domain.Enrollment;
import com.example.peopoolbe.community.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @Operation(summary = "신청하기", description = "원하는 게시글에 신청하기, 거절되면 재신청 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 등록 성공"),
            @ApiResponse(responseCode = "403", description = "토큰 없음"),
            @ApiResponse(responseCode = "500", description = "이미 신청했거나, 게시글 번호가 옳지 않음")
    })
    @PostMapping("/apply/{postId}")
    public ResponseEntity<EnrollmentApplyingRes> applyEnrollment(Principal principal, @PathVariable Long postId, @RequestBody EnrollmentApplyingReq enrollmentApplyingReq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.applyEnrollment(principal, postId, enrollmentApplyingReq));
    }

    @Operation(summary = "멤버가 신청한 목록 조회", description = "토큰으로 멤버 조회 후 그 멤버가 신청한 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "토큰 없음"),
            @ApiResponse(responseCode = "500", description = "기타 오류")
    })
    @GetMapping("/member")
    public ResponseEntity<EnrollmentApplyingList> getApplyingListByMember(Principal principal) {
        return ResponseEntity.ok(enrollmentService.getApplyingListByMember(principal));
    }

    @Operation(summary = "게시글에 신청한 목록 조회", description = "게시글 ID로 게시글 조회 후 그 게시글의 신청 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "토큰 없음"),
            @ApiResponse(responseCode = "500", description = "기타 오류")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<EnrollmentApplyingList> getApplyingListByPost(Principal principal, @PathVariable Long postId) {
        return ResponseEntity.ok(enrollmentService.getApplyingListByPost(principal, postId));
    }

    @Operation(summary = "전체 목록 조회", description = "(관리자 전용) 전체 신청목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "기타 오류")
    })
    @GetMapping
    public ResponseEntity<EnrollmentApplyingList> getEnrollmentList() {
        return ResponseEntity.ok(enrollmentService.getEnrollmentList());
    }

    @Operation(summary = "신청한 내역 취소", description = "토큰으로 멤버 조회 후 그 멤버가 신청한 내역 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "토큰 없음"),
            @ApiResponse(responseCode = "500", description = "게시글이 없거나, 신청 내역이 없음")
    })
    @PatchMapping("/cancel/{postId}")
    public ResponseEntity<Void> cancelEnrollment(Principal principal, @PathVariable Long postId) {
        enrollmentService.cancelEnrollment(principal, postId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "신청 승인", description = "신청 ID값으로 신청 내역 승인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "403", description = "토큰 없음"),
            @ApiResponse(responseCode = "500", description = "신청 내역이 없거나, 게시글의 관리자가 아님")
    })
    @PatchMapping("/approve/{enrollmentId}")
    public ResponseEntity<EnrollmentApplyingRes> approveEnrollment(Principal principal, @PathVariable Long enrollmentId) {
        return ResponseEntity.ok().body(enrollmentService.approveEnrollment(principal, enrollmentId));
    }

    @Operation(summary = "신청 거절", description = "신청 ID값으로 신청 내역 거절")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "403", description = "토큰 없음"),
            @ApiResponse(responseCode = "500", description = "신청 내역이 없거나, 게시글의 관리자가 아님")
    })
    @PatchMapping("/reject/{enrollmentId}")
    public ResponseEntity<EnrollmentApplyingRes> rejectEnrollment(Principal principal, @PathVariable Long enrollmentId) {
        return ResponseEntity.ok().body(enrollmentService.rejectEnrollment(principal, enrollmentId));
    }
}
