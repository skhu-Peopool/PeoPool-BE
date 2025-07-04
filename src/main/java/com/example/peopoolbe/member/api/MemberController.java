package com.example.peopoolbe.member.api;

import com.example.peopoolbe.member.api.dto.request.MemberLoginReq;
import com.example.peopoolbe.member.api.dto.request.MemberSignUpReq;
import com.example.peopoolbe.global.jwt.api.dto.TokenResDto;
import com.example.peopoolbe.member.api.dto.response.UserInfo;
import com.example.peopoolbe.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "자체로그인을 통한 유저 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가입 성공")
    })
    @PostMapping("/signup")
    public ResponseEntity<TokenResDto> signUp(@RequestBody MemberSignUpReq memberSignUpReq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signUp(memberSignUpReq));
    }

    @Operation(summary = "로그인", description = "유저 id, password를 입력하여 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResDto> login(@RequestBody MemberLoginReq memberLoginReq, HttpServletResponse response) {
        return ResponseEntity.ok(memberService.login(memberLoginReq, response));
    }

    @Operation(summary = "유저 정보 확인", description = "토큰을 통한 유저 정보 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @GetMapping("/user")
    public ResponseEntity<UserInfo> getUserInfo(Principal principal) {
        return ResponseEntity.ok(memberService.getUserInfo(principal));
    }
}
