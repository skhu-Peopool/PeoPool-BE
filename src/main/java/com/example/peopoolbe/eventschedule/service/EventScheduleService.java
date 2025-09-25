package com.example.peopoolbe.eventschedule.service;

import com.example.peopoolbe.eventschedule.api.req.MakeEventReq;
import com.example.peopoolbe.eventschedule.api.res.EventInfo;
import com.example.peopoolbe.eventschedule.domain.EventSchedule;
import com.example.peopoolbe.eventschedule.domain.EventScheduleType;
import com.example.peopoolbe.eventschedule.domain.repository.EventScheduleRepository;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventScheduleService {

    private final EventScheduleRepository eventScheduleRepository;
    private final MemberService memberService;

    @Transactional
    public EventInfo addPersonalEvent(Principal principal, MakeEventReq makeEventReq) {
        Member member = memberService.getUserByToken(principal);

        EventSchedule eventSchedule = EventSchedule.builder()
                .owner(member)
                .description(makeEventReq.description())
                .startDate(makeEventReq.startDate())
                .endDate(makeEventReq.endDate())
                .isAllDay(makeEventReq.isAllDay())
                .startTime(makeEventReq.startTime() != null ? makeEventReq.startTime() : null)
                .endTime(makeEventReq.endTime() != null ? makeEventReq.endTime() : null)
                .type(EventScheduleType.PERSONAL)
                .build();
        eventScheduleRepository.save(eventSchedule);

        return EventInfo.from(eventSchedule);
    }

    @Transactional(readOnly = true)
    public EventInfo getEventInfo(Principal principal, Long eventId) {
        Member member = memberService.getUserByToken(principal);
        EventSchedule eventSchedule = eventScheduleRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("No event schedule found with id " + eventId));

        if (eventSchedule.getType() == EventScheduleType.TEAM) {} // 팀의 일정일 경우 권한처리, return까지

        if (!eventSchedule.getOwner().equals(member)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 스케줄에 접근할 권한이 없습니다.");
        }
        return EventInfo.from(eventSchedule);
    }

    @Transactional(readOnly = true)
    public List<EventInfo> getMyEvents(Principal principal) {
        Member member = memberService.getUserByToken(principal);
        List<EventSchedule> eventScheduleList = eventScheduleRepository.findAllByOwner(member);

        return eventScheduleList.stream()
                .map(EventInfo::from)
                .toList();
    }

    @Transactional
    public EventInfo updateEvent(Principal principal, Long eventId, MakeEventReq makeEventReq) {
        Member member = memberService.getUserByToken(principal);
        EventSchedule eventSchedule = eventScheduleRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("No event schedule found with id " + eventId));

        if (!eventSchedule.getOwner().equals(member)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 스케줄에 접근할 권한이 없습니다.");
        }

        eventSchedule.updateEvent(
                makeEventReq.description(),
                makeEventReq.startDate(),
                makeEventReq.endDate(),
                makeEventReq.isAllDay(),
                makeEventReq.startTime(),
                makeEventReq.endTime()
        );
        eventScheduleRepository.save(eventSchedule);

        return EventInfo.from(eventSchedule);
    }

    @Transactional
    public void deleteEvent(Principal principal, Long eventId) {
        Member member = memberService.getUserByToken(principal);
        EventSchedule eventSchedule = eventScheduleRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("No event schedule found with id " + eventId));

        if (!eventSchedule.getOwner().equals(member)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 스케줄에 접근할 권한이 없습니다.");
        }
        eventScheduleRepository.delete(eventSchedule);
    }
}
