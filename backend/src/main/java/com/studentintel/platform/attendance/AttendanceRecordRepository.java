package com.studentintel.platform.attendance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository
        extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findBySessionId(Long sessionId);

    List<AttendanceRecord> findByStudentId(Long studentId);

    boolean existsBySessionIdAndStudentId(
            Long sessionId,
            Long studentId);
}