package com.example.peopoolbe.community.domain;

import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
    private LocalDateTime recruitmentStartDate; // 이거까지 만들고 접었음 7/22 03:30

    @Column(name = "RECRUITMENT_END_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime recruitmentEndDate;

    @Column(name = "MAXIMUM_PEOPLE")
    private Integer maximumPeople;

    @Column(name = "POST_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Post(String title, String content, LocalDateTime recruitmentStartDate, LocalDateTime recruitmentEndDate, Integer maximumPeople, Status status, Member member) {
        this.title = title;
        this.content = content;
        this.recruitmentStartDate = recruitmentStartDate;
        this.recruitmentEndDate = recruitmentEndDate;
        this.maximumPeople = maximumPeople;
        this.status = status;
        this.member = member;
    }

    public void update(String title, String content, LocalDateTime recruitmentStartDate, LocalDateTime recruitmentEndDate, Integer maximumPeople, Status status) {
        this.title = title;
        this.content = content;
        this.recruitmentStartDate = recruitmentStartDate;
        this.recruitmentEndDate = recruitmentEndDate;
        this.maximumPeople = maximumPeople;
        this.status = status;
    }
}
