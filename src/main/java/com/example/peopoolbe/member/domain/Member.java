package com.example.peopoolbe.member.domain;

import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.global.jwt.domain.RefreshToken;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;

    @Column(name = "USER_PASSWORD", nullable = false)
    private String password;

    @Column(name = "USER_NAME", nullable = false)
    private String name;

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USER_PROFILE_IMG")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE", nullable = false)
    private Role role;

//    @OneToOne
//    @JoinColumn(name = "ID")
//    private RefreshToken refreshToken;

    @Builder
    public Member(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = Role.ROLE_USER;
    }
}
