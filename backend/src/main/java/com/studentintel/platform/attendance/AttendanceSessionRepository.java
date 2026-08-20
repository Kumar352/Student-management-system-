package com.studentintel.platform.attendance;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceSessionRepository
        extends JpaRepository<AttendanceSession, Long> {

    List<AttendanceSession> findByCourseId(Long courseId);

    List<AttendanceSession> findBySemesterId(Long semesterId);

    List<AttendanceSession> findByCourseIdAndSessionDateBetween(
            Long courseId,
            LocalDate startDate,
            LocalDate endDate);
}