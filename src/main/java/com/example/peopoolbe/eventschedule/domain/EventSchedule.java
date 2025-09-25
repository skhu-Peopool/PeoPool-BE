package com.example.peopoolbe.eventschedule.domain;

import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.global.entity.BaseEntity;
import com.example.peopoolbe.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class EventSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean isAllDay;

    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private EventScheduleType type;

    @Builder
    public EventSchedule(Member owner, String description,
                         LocalDate startDate, LocalDate endDate, boolean isAllDay,
                         LocalTime startTime, LocalTime endTime, EventScheduleType type) {
        this.owner = owner;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAllDay = isAllDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public void updateEvent(String description, LocalDate startDate, LocalDate endDate,
                            boolean isAllDay, LocalTime startTime, LocalTime endTime) {
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAllDay = isAllDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
