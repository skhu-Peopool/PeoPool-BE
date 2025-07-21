package com.example.peopoolbe.member.domain;

import com.example.peopoolbe.community.domain.Post;
import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.global.jwt.domain.RefreshToken;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Column(name = "USER_PASSWORD", nullable = false)
    private String password;

    @Column(name = "USER_NICKNAME", nullable = false, unique = true)
    private String nickname;

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USER_PROFILE_IMG")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_PROFILE_VISIBLE", nullable = false)
    private ViewStatus profileVisible;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ACTIVITY_VISIBLE", nullable = false)
    private ViewStatus activityVisible;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_POSTING_VISIBLE", nullable = false)
    private ViewStatus postVisible;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

//    @OneToOne
//    @JoinColumn(name = "ID")
//    private RefreshToken reissueTokens;

    @Builder
    public Member(String password, String nickname, String email, String profileImage, List<Post> posts, ViewStatus profileVisible, ViewStatus activityVisible, ViewStatus postVisible) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = Role.ROLE_USER;
        this.profileImage = profileImage;
        this.posts = posts;
        this.profileVisible = profileVisible;
        this.activityVisible = activityVisible;
        this.postVisible = postVisible;
    }

    public void updateProfile(String nickname, ViewStatus profileVisible, ViewStatus activityVisible, ViewStatus postVisible) {
        this.nickname = nickname;
        this.profileVisible = profileVisible;
        this.activityVisible = activityVisible;
        this.postVisible = postVisible;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
