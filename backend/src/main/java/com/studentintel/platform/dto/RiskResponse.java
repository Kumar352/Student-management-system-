package com.studentintel.platform.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.studentintel.platform.risk.RiskAssessment;

public record RiskResponse(
        Long id,
        Long studentId,
        Long semesterId,
        String riskLevel,
        BigDecimal riskScore,
        BigDecimal attendanceScore,
        BigDecimal academicScore,
        BigDecimal assessmentScore,
        BigDecimal trendScore,
        String explanation,
        LocalDateTime calculatedAt) {

    public static RiskResponse from(RiskAssessment risk) {
        return new RiskResponse(
                risk.getId(),
                risk.getStudent().getId(),
                risk.getSemester().getId(),
                risk.getRiskLevel(),
                risk.getRiskScore(),
                risk.getAttendanceScore(),
                risk.getAcademicScore(),
                risk.getAssessmentScore(),
                risk.getTrendScore(),
                risk.getExplanation(),
                risk.getCalculatedAt());
    }
}