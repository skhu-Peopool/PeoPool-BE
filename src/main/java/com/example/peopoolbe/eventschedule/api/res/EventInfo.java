package com.example.peopoolbe.eventschedule.api.res;

import com.example.peopoolbe.eventschedule.domain.EventSchedule;
import com.example.peopoolbe.eventschedule.domain.EventScheduleType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record EventInfo(
        Long ownerId,
        String ownerName,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        boolean isAllDay,
        LocalTime startTime,
        LocalTime endTime,
        EventScheduleType type
) {
    public static EventInfo from(EventSchedule eventSchedule) {
        return EventInfo.builder()
                .ownerId(eventSchedule.getOwner().getId())
                .ownerName(eventSchedule.getOwner().getNickname())
                .description(eventSchedule.getDescription())
                .startDate(eventSchedule.getStartDate())
                .endDate(eventSchedule.getEndDate())
                .isAllDay(eventSchedule.isAllDay())
                .startTime(eventSchedule.getStartTime())
                .endTime(eventSchedule.getEndTime())
                .type(eventSchedule.getType())
                .build();
    }
}
