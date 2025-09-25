package com.example.peopoolbe.eventschedule.domain.repository;

import com.example.peopoolbe.eventschedule.domain.EventSchedule;
import com.example.peopoolbe.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {

    // 특정 날짜(date)가 일정 구간(startDate ~ endDate)에 포함된 일정 조회
    @Query("SELECT e FROM EventSchedule e " +
            "WHERE :date BETWEEN e.startDate AND e.endDate " +
            "AND e.owner.id = :ownerId")
    List<EventSchedule> findEventsByDate(@Param("ownerId") Long ownerId,
                                         @Param("date") LocalDate date);

    // 월별 일정 조회 (달력 뷰용) - 일정이 걸쳐 있더라도 다 포함
    @Query("SELECT e FROM EventSchedule e " +
            "WHERE e.owner.id = :ownerId " +
            "AND (e.startDate <= :endDate AND e.endDate >= :startDate)")
    List<EventSchedule> findEventsBetweenDates(@Param("ownerId") Long ownerId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    List<EventSchedule> findAllByOwner(Member owner);
}

