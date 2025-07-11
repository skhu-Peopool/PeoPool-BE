package com.example.peopoolbe.member.service;

import com.example.peopoolbe.global.jwt.domain.RefreshToken;
import com.example.peopoolbe.global.jwt.domain.repository.RefreshTokenRepository;
import com.example.peopoolbe.global.jwt.service.TokenProvider;
import com.example.peopoolbe.global.s3.dto.S3ImageUploadRes;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public TokenResDto signUp(MemberSignUpReq memberSignUpReq, HttpServletResponse response) {
        String defaultImage = "https://" + bucket + ".s3." + region + ".amazonaws.com/default.png";
        Member member = Member.builder()
                .userId(memberSignUpReq.userId())
                .nickname(memberSignUpReq.nickname())
                .email(memberSignUpReq.email())
                .password(passwordEncoder.encode(memberSignUpReq.password()))
                .profileImage(defaultImage)
                .profileVisible(ProfileVisible.INVISIBLE)
                .build();
        memberRepository.save(member);

        TokenResDto tokenResDto = tokenProvider.createToken(member);
        member.addRefreshToken(refreshTokenRepository.findByRefreshToken(tokenResDto.refreshToken()));

        addRefreshTokenInCookie(tokenResDto, response);

        return tokenResDto;
    }

    public TokenResDto login(MemberLoginReq memberLoginReq, HttpServletResponse response) {
        Member member = memberRepository.findByUserId(memberLoginReq.id())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 멤버"));

        if (!passwordEncoder.matches(memberLoginReq.password(), member.getPassword()))
            throw new RuntimeException("비밀번호 불일치");

        TokenResDto tokenResDto = tokenProvider.createToken(member);

        member.addRefreshToken(refreshTokenRepository.findByRefreshToken(tokenResDto.refreshToken()));

//        Cookie accessCookie = new Cookie("accessToken", tokenProvider.createToken(member).accessToken());
//        accessCookie.setPath("/");
//        accessCookie.setHttpOnly(true);
//        accessCookie.setSecure(true);
//        accessCookie.setMaxAge(60 * 15); // 15분

        addRefreshTokenInCookie(tokenResDto, response);

        return tokenResDto;
    }

    public void addRefreshTokenInCookie(TokenResDto tokenResDto, HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", tokenResDto.refreshToken());
        refreshCookie.setPath("/");
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setAttribute("SameSite", "None");
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주

        response.addCookie(refreshCookie);
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

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        Member member = getUserByToken(principal);
//        member.addRefreshToken(null);
//
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        Cookie refreshCookie1 = new Cookie("refreshCookie", refreshToken);
        refreshCookie1.setPath("/");
        refreshCookie1.setMaxAge(0);
        response.addCookie(refreshCookie1);
    }
}
