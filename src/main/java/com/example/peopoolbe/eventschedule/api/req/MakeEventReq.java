package com.example.peopoolbe.eventschedule.api.req;

import java.time.LocalDate;
import java.time.LocalTime;

public record MakeEventReq(
        String description,
        LocalDate startDate,
        LocalDate endDate,
        boolean isAllDay,
        LocalTime startTime,
        LocalTime endTime
) {
}
