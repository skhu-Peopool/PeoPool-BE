package com.example.peopoolbe.member.service;

import com.example.peopoolbe.global.jwt.domain.RefreshToken;
import com.example.peopoolbe.global.jwt.domain.repository.RefreshTokenRepository;
import com.example.peopoolbe.global.jwt.service.TokenProvider;
import com.example.peopoolbe.member.api.dto.request.MemberLoginReq;
import com.example.peopoolbe.member.api.dto.request.MemberProfileUpdateReq;
import com.example.peopoolbe.member.api.dto.request.MemberSignUpReq;
import com.example.peopoolbe.global.jwt.api.dto.TokenResDto;
import com.example.peopoolbe.member.api.dto.response.UserInfo;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.ProfileVisible;
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
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResDto signUp(MemberSignUpReq memberSignUpReq) {
        Member member = memberRepository.save(Member.builder()
                .userId(memberSignUpReq.userId())
                .nickname(memberSignUpReq.nickname())
                .email(memberSignUpReq.email())
                .password(passwordEncoder.encode(memberSignUpReq.password()))
                .profileImage("")
                .profileVisible(ProfileVisible.INVISIBLE)
                .build());

        return tokenProvider.createToken(member);
    }

    public TokenResDto login(MemberLoginReq memberLoginReq, HttpServletResponse response) {
        Member member = memberRepository.findByUserId(memberLoginReq.id())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 멤버"));

        if (!passwordEncoder.matches(memberLoginReq.password(), member.getPassword()))
            throw new RuntimeException("비밀번호 불일치");

        RefreshToken refreshToken = refreshTokenRepository.findById(member.getId())
                .orElseThrow(() -> new EntityNotFoundException("리프레쉬 토큰 없음"));

        TokenResDto tokenResDto = tokenProvider.refreshToken(refreshToken.getRefreshToken());

//        Cookie accessCookie = new Cookie("accessToken", tokenProvider.createToken(member).accessToken());
//        accessCookie.setPath("/");
//        accessCookie.setHttpOnly(true);
//        accessCookie.setSecure(true);
//        accessCookie.setMaxAge(60 * 15); // 15분

        Cookie refreshCookie = new Cookie("refreshToken", tokenResDto.refreshToken());
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주

//        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return tokenResDto;
    }

    public UserInfo getUserInfo(Principal principal) {
        Member member = getUserByToken(principal);

        return UserInfo.builder()
                .userId(member.getUserId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .email(member.getEmail())
                .profileVisible(member.getProfileVisible())
                .build();
    }

    public UserInfo updateUserInfo(Principal principal, MemberProfileUpdateReq memberProfileUpdateReq) {
        Member member = getUserByToken(principal);

        member.update(passwordEncoder.encode(memberProfileUpdateReq.password()),
                memberProfileUpdateReq.nickname(),
                memberProfileUpdateReq.profileImage(),
                memberProfileUpdateReq.profileVisible());

        memberRepository.save(member);

        return UserInfo.builder()
                .userId(member.getUserId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .email(member.getEmail())
                .profileVisible(member.getProfileVisible())
                .build();
    }

    public Member getUserByToken(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }
}
