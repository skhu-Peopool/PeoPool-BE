package com.example.peopoolbe.global.s3.domain;

import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Image extends BaseEntity {

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Image(String path, String fileName, ImageType imageType, Member member, Post post) {
        this.path = path;
        this.fileName = fileName;
        this.imageType = imageType;
        this.member = member;
        this.post = post;
    }
}
