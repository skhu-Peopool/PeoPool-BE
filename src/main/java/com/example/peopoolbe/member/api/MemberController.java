package com.example.peopoolbe.member.api;

import com.example.peopoolbe.global.s3.dto.S3ImageUploadRes;
import com.example.peopoolbe.global.s3.service.S3Service;
import com.example.peopoolbe.member.api.dto.request.*;
import com.example.peopoolbe.global.jwt.api.dto.TokenResDto;
import com.example.peopoolbe.member.api.dto.response.UserInfo;
import com.example.peopoolbe.member.domain.ViewStatus;
import com.example.peopoolbe.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final S3Service s3Service;

    @Operation(summary = "회원가입", description = "자체로그인을 통한 유저 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가입 성공")
    })
    @PostMapping("/signup")
    public ResponseEntity<TokenResDto> signUp(@RequestBody @Valid MemberSignUpReq memberSignUpReq, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signUp(memberSignUpReq, response));
    }

    @Operation(summary = "로그인", description = "유저 id, password를 입력하여 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResDto> login(@RequestBody @Valid MemberLoginReq memberLoginReq, HttpServletResponse response) {
        return ResponseEntity.ok(memberService.login(memberLoginReq, response));
    }

    @Operation(summary = "유저 정보 확인", description = "토큰을 통한 유저 정보 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음")
    })
    @GetMapping("/user")
    public ResponseEntity<UserInfo> getUserInfo(Principal principal) {
        return ResponseEntity.ok(memberService.getUserInfo(principal));
    }

    @Operation(summary = "유저 정보 수정", description = "이름, 공개여부 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "뭔가 하나 안넣음")
    })
    @PatchMapping("/update/profile")
    public ResponseEntity<UserInfo> updateUserInfo(Principal principal, @RequestBody MemberProfileUpdateReq memberProfileUpdateReq) {
        return ResponseEntity.ok(memberService.updateUserInfo(principal, memberProfileUpdateReq));
    }

    @Operation(summary = "유저 프로필 공개여부 수정", description = "다른 사람 찾기 페이지에 보여줄지에 대한 프로필 공개 여부 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "뭔가 하나 안넣음")
    })
    @PatchMapping("/update/profilevisible")
    public ResponseEntity<ViewStatus> updateProfileVisibility(Principal principal, @RequestBody MemberVisibilityUpdateReq memberVisibilityUpdateReq) {
        return ResponseEntity.ok(memberService.updateProfileVisibility(principal, memberVisibilityUpdateReq));
    }

    @Operation(summary = "유저 활동 공개여부 수정", description = "타인이 프로필 진입시 사용자의 활동 공개 여부")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "뭔가 하나 안넣음")
    })
    @PatchMapping("/update/activityvisible")
    public ResponseEntity<ViewStatus> updateActivityVisibility(Principal principal, @RequestBody MemberVisibilityUpdateReq memberVisibilityUpdateReq) {
        return ResponseEntity.ok(memberService.updateActivityVisibility(principal, memberVisibilityUpdateReq));
    }

    @Operation(summary = "유저 게시글 공개여부 수정", description = "타인이 프로필 진입시 사용자가 작성한 게시글 공개 여부")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "뭔가 하나 안넣음")
    })
    @PatchMapping("/update/postvisible")
    public ResponseEntity<ViewStatus> updatePostVisiblity(Principal principal, @RequestBody MemberVisibilityUpdateReq memberVisibilityUpdateReq) {
        return ResponseEntity.ok(memberService.updatePostVisibility(principal, memberVisibilityUpdateReq));
    }

    @Operation(summary = "프로필 수정 - 유저 암호 변경", description = "프로필 수정 페이지에서 암호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음"),
            @ApiResponse(responseCode = "500", description = "뭔가 하나 안넣었거나, 기존 암호와 불일치")
    })
    @PatchMapping("/pwd")
    public ResponseEntity<UserInfo> updateUserPwd(Principal principal, @RequestBody @Valid MemberPwdUpdateReq memberPwdUpdateReq) {
        return ResponseEntity.ok(memberService.updatePwd(principal, memberPwdUpdateReq));
    }

    @Operation(summary = "로그인 - 유저 암호 변경", description = "암호를 잊었을 경우 로그인 페이지에서 암호 변경(이메일 코드 전송 활용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "500", description = "뭔가 하나가 안들어갔다고 할 수 있음")
    })
    @PatchMapping("/forgotpwd")
    public ResponseEntity<Void> forgotUserPwd(@RequestBody @Valid MemberPwdForgotReq memberPwdForgotReq) {
        memberService.changePwd(memberPwdForgotReq);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃", description = "유저 로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        memberService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필사진 업로드", description = "유저의 프로필사진 업로드, 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사진 수정 성공"),
            @ApiResponse(responseCode = "500", description = "Multipartfile 형식의 사진을 주지 않았을 확률이 매우 높음")
    })
    @PostMapping("/image")
    public ResponseEntity<S3ImageUploadRes> uploadImage(Principal principal, MultipartFile file) {
        return ResponseEntity.ok(s3Service.uploadProfileImage(principal, file));
    }
}
