package com.studentintel.platform.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.studentintel.platform.performance.PerformanceRecord;

public record PerformanceResponse(
        Long id,
        Long studentId,
        Long semesterId,
        BigDecimal gpa,
        BigDecimal cgpa,
        BigDecimal creditsAttempted,
        BigDecimal creditsEarned,
        String academicStatus,
        LocalDateTime calculatedAt) {

    public static PerformanceResponse from(PerformanceRecord record) {
        return new PerformanceResponse(
                record.getId(),
                record.getStudent().getId(),
                record.getSemester().getId(),
                record.getGpa(),
                record.getCgpa(),
                record.getCreditsAttempted(),
                record.getCreditsEarned(),
                record.getAcademicStatus(),
                record.getCalculatedAt());
    }
}