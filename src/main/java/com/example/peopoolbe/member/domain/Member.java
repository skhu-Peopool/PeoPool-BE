package com.example.peopoolbe.member.domain;

import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.global.s3.domain.Image;
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "USER_PROFILE_IMAGE_ID")
    private Image profileImage;

    @Column(name = "USER_MAIN_INTRODUCTION")
    private String mainIntroduction;

    @Column(name = "USER_SUB_INTRODUCTION")
    private String subIntroduction;

    @Column(name = "USER_PROFILE_TAG")
    private String hashtag;

    @Column(name = "USER_KAKAOTALK_ID")
    private String kakaoId;

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

//    @OneToOne
//    @JoinColumn(name = "ID")
//    private RefreshToken reissueTokens;

    @Builder
    public Member(String password, String nickname, String email,
                  Image profileImage, String mainIntroduction, String subIntroduction,
                  String hashtag, String kakaoId, List<Post> posts, ViewStatus profileVisible,
                  ViewStatus activityVisible, ViewStatus postVisible) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = Role.ROLE_USER;
        this.profileImage = profileImage;
        this.mainIntroduction = mainIntroduction;
        this.subIntroduction = subIntroduction;
        this.hashtag = hashtag;
        this.kakaoId = kakaoId;
        this.posts = posts;
        this.profileVisible = profileVisible;
        this.activityVisible = activityVisible;
        this.postVisible = postVisible;
    }

    public void updateProfile(
            String nickname, String mainIntroduction, String subIntroduction,
            String hashtag, String kakaoId, Image profileImage) {
        this.nickname = nickname;
        this.mainIntroduction = mainIntroduction;
        this.subIntroduction = subIntroduction;
        this.hashtag = hashtag;
        this.kakaoId = kakaoId;
        this.profileImage = profileImage;
    }

    public void updateProfileVisibility(ViewStatus profileVisible) {
        this.profileVisible = profileVisible;
    }

    public void updateActivityVisibility(ViewStatus activityVisible) {
        this.activityVisible = activityVisible;
    }

    public void updatePostVisibility(ViewStatus postVisible) {
        this.postVisible = postVisible;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

//    public void updateImage(String profileImage) {
//        this.profileImage = profileImage;
//    }
}
