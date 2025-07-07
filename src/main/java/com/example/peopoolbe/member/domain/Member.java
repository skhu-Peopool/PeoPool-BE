package com.example.peopoolbe.member.domain;

import com.example.peopoolbe.community.domain.Post;
import com.example.peopoolbe.global.entity.BaseEntity;
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

    @Column(name = "USER_CUSTOMID", nullable = false, unique = true)
    private String userId;

    @Column(name = "USER_PASSWORD", nullable = false)
    private String password;

    @Column(name = "USER_NICKNAME", nullable = false)
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
    private ProfileVisible profileVisible;

//    @OneToOne
//    @JoinColumn(name = "ID")
//    private RefreshToken refreshToken;

    @Builder
    public Member(String userId, String password, String nickname, String email, String profileImage, List<Post> posts, ProfileVisible profileVisible) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = Role.ROLE_USER;
        this.profileImage = profileImage;
        this.posts = posts;
        this.profileVisible = profileVisible;
    }

    public void update(String password, String nickname, String profileImage, ProfileVisible profileVisible) {
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.profileVisible = profileVisible;
    }
}
