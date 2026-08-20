package com.studentintel.platform.performance;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRecordRepository
        extends JpaRepository<PerformanceRecord, Long> {

    List<PerformanceRecord> findByStudentId(Long studentId);

    List<PerformanceRecord> findBySemesterId(Long semesterId);

    Optional<PerformanceRecord> findByStudentIdAndSemesterId(
            Long studentId,
            Long semesterId);
}