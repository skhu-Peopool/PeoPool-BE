package com.example.peopoolbe.member.service;

import com.example.peopoolbe.global.jwt.domain.repository.RefreshTokenRepository;
import com.example.peopoolbe.global.jwt.service.TokenProvider;
import com.example.peopoolbe.global.s3.service.S3Service;
import com.example.peopoolbe.member.api.dto.request.*;
import com.example.peopoolbe.global.jwt.api.dto.TokenResDto;
import com.example.peopoolbe.member.api.dto.response.UserInfo;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.ViewStatus;
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
    private final S3Service s3Service;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public TokenResDto signUp(MemberSignUpReq memberSignUpReq, HttpServletResponse response) {
        String defaultImage = "https://" + bucket + ".s3." + region + ".amazonaws.com/default.png";
        Member member = Member.builder()
                .nickname(memberSignUpReq.nickname())
                .email(memberSignUpReq.email())
                .password(passwordEncoder.encode(memberSignUpReq.password()))
                .profileImage(defaultImage)
                .profileVisible(ViewStatus.INVISIBLE)
                .activityVisible(ViewStatus.INVISIBLE)
                .postVisible(ViewStatus.INVISIBLE)
                .build();
        memberRepository.save(member);

        TokenResDto tokenResDto = tokenProvider.createToken(member);
//        member.addRefreshToken(refreshTokenRepository.findByRefreshToken(tokenResDto.refreshToken()));

        addRefreshTokenInCookie(tokenResDto, response);

        return tokenResDto;
    }

    public TokenResDto login(MemberLoginReq memberLoginReq, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(memberLoginReq.email())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 멤버"));

        if (!passwordEncoder.matches(memberLoginReq.password(), member.getPassword()))
            throw new RuntimeException("비밀번호 불일치");

        TokenResDto tokenResDto = tokenProvider.createToken(member);

//        member.addRefreshToken(refreshTokenRepository.findByRefreshToken(tokenResDto.refreshToken()));

        addRefreshTokenInCookie(tokenResDto, response);

        return tokenResDto;
    }

    public void addRefreshTokenInCookie(TokenResDto tokenResDto, HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", tokenResDto.refreshToken());
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setAttribute("SameSite", "None");
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주

        response.addCookie(refreshCookie);
    }

    public UserInfo getUserInfo(Principal principal) {
        Member member = getUserByToken(principal);

        return UserInfo.from(member);
    }

    public UserInfo updateUserInfo(Principal principal, MemberProfileUpdateReq memberProfileUpdateReq, MultipartFile image) {
        Member member = getUserByToken(principal);

        member.updateProfile(memberProfileUpdateReq.nickname(),
                memberProfileUpdateReq.mainIntroduction(),
                memberProfileUpdateReq.subIntroduction(),
                memberProfileUpdateReq.hashtag(),
                memberProfileUpdateReq.kakaoId(),
                s3Service.uploadProfileImage(image, member));

        memberRepository.save(member);

        return UserInfo.from(member);
    }

    public ViewStatus updateProfileVisibility(Principal principal, MemberVisibilityUpdateReq memberVisibilityUpdateReq) {
        Member member = getUserByToken(principal);
        member.updateProfileVisibility(memberVisibilityUpdateReq.visible());
        memberRepository.save(member);

        return member.getProfileVisible();
    }

    public ViewStatus updateActivityVisibility(Principal principal, MemberVisibilityUpdateReq memberVisibilityUpdateReq) {
        Member member = getUserByToken(principal);
        member.updateActivityVisibility(memberVisibilityUpdateReq.visible());
        memberRepository.save(member);

        return member.getActivityVisible();
    }

    public ViewStatus updatePostVisibility(Principal principal, MemberVisibilityUpdateReq memberVisibilityUpdateReq) {
        Member member = getUserByToken(principal);
        member.updatePostVisibility(memberVisibilityUpdateReq.visible());
        memberRepository.save(member);

        return member.getPostVisible();
    }

    public void changePwd(MemberPwdForgotReq memberPwdForgotReq) {
        Member member = memberRepository.findByEmail(memberPwdForgotReq.email())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 멤버"));

        member.updatePassword(passwordEncoder.encode(memberPwdForgotReq.newPassword()));
        memberRepository.save(member);
    }

    public UserInfo updatePwd(Principal principal, MemberPwdUpdateReq memberPwdUpdateReq) {
        Member member = getUserByToken(principal);
        if(!passwordEncoder.matches(memberPwdUpdateReq.password(), member.getPassword()))
            throw new IllegalArgumentException("일치하지 않는 비밀번호");

        member.updatePassword(passwordEncoder.encode(memberPwdUpdateReq.newPassword()));
        memberRepository.save(member);

        return UserInfo.builder()
                .id(member.getId())
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
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            System.out.println(cookie.getName() + " + " + cookie.getValue());
        }

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if(refreshToken != null) {
            refreshTokenRepository.deleteByRefreshToken(refreshToken);
        }

        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setAttribute("SameSite", "None");
        response.addCookie(refreshCookie);
    }
}
