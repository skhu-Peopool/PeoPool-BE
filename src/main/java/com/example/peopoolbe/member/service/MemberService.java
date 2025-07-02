package com.example.peopoolbe.member.service;

import com.example.peopoolbe.global.jwt.service.TokenProvider;
import com.example.peopoolbe.member.api.dto.request.MemberLoginReq;
import com.example.peopoolbe.member.api.dto.request.MemberSignUpReq;
import com.example.peopoolbe.global.jwt.dto.TokenResDto;
import com.example.peopoolbe.member.api.dto.response.UserInfo;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public TokenResDto signUp(MemberSignUpReq memberSignUpReq) {
        Member member = memberRepository.save(Member.builder()
                .userId(memberSignUpReq.userId())
                .name(memberSignUpReq.name())
                .email(memberSignUpReq.email())
                .password(passwordEncoder.encode(memberSignUpReq.password()))
                .build());

        return tokenProvider.createToken(member);
    }

    public TokenResDto login(MemberLoginReq memberLoginReq, HttpServletResponse response) {
        Member member = memberRepository.findByUserId(memberLoginReq.id())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 멤버"));

        if (!passwordEncoder.matches(memberLoginReq.password(), member.getPassword()))
            throw new RuntimeException("비밀번호 불일치");

//        Cookie accessCookie = new Cookie("accessToken", tokenProvider.createToken(member).accessToken());
//        accessCookie.setPath("/");
//        accessCookie.setHttpOnly(true);
//        accessCookie.setSecure(true);
//        accessCookie.setMaxAge(60 * 15); // 15분

        Cookie refreshCookie = new Cookie("refreshToken", tokenProvider.createToken(member).refreshToken());
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주

//        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return tokenProvider.createToken(member);
    }

    public UserInfo getUserInfo(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return UserInfo.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .email(member.getEmail())
                .build();
    }
}
