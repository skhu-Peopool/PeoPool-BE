package com.example.peopoolbe.community.post.domain;

import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.global.s3.domain.Image;
import com.example.peopoolbe.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "RECRUITMENT_START_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentStartDate;

    @Column(name = "RECRUITMENT_END_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentEndDate;

    @Column(name = "ACTIVITY_START_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityStartDate;

    @Column(name = "MAXIMUM_PEOPLE")
    private Integer maximumPeople;

    @Column(name = "APPROVED_PEOPLE")
    private Integer approvedPeople;

    @Column(name = "APPLIED_PEOPLE")
    private Integer appliedPeople;

    @Column(name = "POST_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Column(name = "POST_CATEGORY", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> image = new ArrayList<>();

    @Column(name = "POST_VIEWS")
    private Integer views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Post(String title, String content, LocalDate recruitmentStartDate,
                LocalDate recruitmentEndDate, LocalDate activityStartDate, Integer maximumPeople,
                Integer approvedPeople, Integer appliedPeople, PostStatus postStatus, Category category,
                Member member, Integer views) {
        this.title = title;
        this.content = content;
        this.recruitmentStartDate = recruitmentStartDate;
        this.recruitmentEndDate = recruitmentEndDate;
        this.activityStartDate = activityStartDate;
        this.maximumPeople = maximumPeople;
        this.approvedPeople = approvedPeople;
        this.appliedPeople = appliedPeople;
        this.postStatus = postStatus;
        this.member = member;
        this.category = category;
        this.views = views;
    }

    public void update(String title, String content, LocalDate recruitmentStartDate,
                       LocalDate recruitmentEndDate, LocalDate activityStartDate, Integer maximumPeople,
                       PostStatus postStatus, Category category) {
        this.title = title;
        this.content = content;
        this.recruitmentStartDate = recruitmentStartDate;
        this.recruitmentEndDate = recruitmentEndDate;
        this.activityStartDate = activityStartDate;
        this.maximumPeople = maximumPeople;
        this.postStatus = postStatus;
        this.category = category;
    }

    public void updateStatus(PostStatus postStatus){
        this.postStatus = postStatus;
    }

    public void updateImages(List<Image> images){
        this.image.clear();
        if(images != null)
            this.image.addAll(images);
    }

    public void updateApprovedPeople(Integer approvedPeople) { this.approvedPeople = approvedPeople; }
    public void updateAppliedPeople(Integer appliedPeople) { this.appliedPeople = appliedPeople; }
    public void incrementViews() { this.views++; }
}
