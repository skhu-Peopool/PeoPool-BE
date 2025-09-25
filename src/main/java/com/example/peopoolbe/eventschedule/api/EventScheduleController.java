package com.example.peopoolbe.eventschedule.api;

import com.example.peopoolbe.eventschedule.api.req.MakeEventReq;
import com.example.peopoolbe.eventschedule.api.res.EventInfo;
import com.example.peopoolbe.eventschedule.service.EventScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class EventScheduleController {

    private final EventScheduleService eventScheduleService;

    @PostMapping("/personal")
    public ResponseEntity<EventInfo> addPersonalEvent(Principal principal, @RequestBody MakeEventReq makeEventReq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventScheduleService.addPersonalEvent(principal, makeEventReq));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventInfo> getEventInfo(Principal principal, @PathVariable Long eventId) {
        return ResponseEntity.ok().body(eventScheduleService.getEventInfo(principal, eventId));
    }

    @GetMapping
    public ResponseEntity<List<EventInfo>> getMyEvents(Principal principal) {
        return ResponseEntity.ok().body(eventScheduleService.getMyEvents(principal));
    }

    @PatchMapping("/edit/{eventId}")
    public ResponseEntity<EventInfo> updateEvent(Principal principal, @PathVariable Long eventId, @RequestBody MakeEventReq makeEventReq) {
        return ResponseEntity.ok().body(eventScheduleService.updateEvent(principal, eventId, makeEventReq));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(Principal principal, @PathVariable Long eventId) {
        eventScheduleService.deleteEvent(principal, eventId);
        return ResponseEntity.ok().build();
    }
}
