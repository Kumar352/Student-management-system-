package com.studentintel.platform.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentintel.platform.risk.RiskAssessment;
import com.studentintel.platform.risk.RiskAssessmentRepository;
import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.student.Student;

@Service
public class RiskService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AttendanceService attendanceService;
    private final PerformanceService performanceService;

    public RiskService(
            RiskAssessmentRepository riskAssessmentRepository,
            AttendanceService attendanceService,
            PerformanceService performanceService) {

        this.riskAssessmentRepository = riskAssessmentRepository;
        this.attendanceService = attendanceService;
        this.performanceService = performanceService;
    }

    @Transactional
    public RiskAssessment calculateRisk(
            Student student,
            Semester semester,
            BigDecimal academicScore,
            BigDecimal trendScore) {

        BigDecimal attendanceScore =
                attendanceService.calculateAttendancePercentage(
                        student.getId());

        BigDecimal assessmentScore =
                performanceService.calculateAssessmentScore(
                        student,
                        semester);

        BigDecimal riskScore = calculateRiskScore(
                attendanceScore,
                academicScore,
                assessmentScore,
                trendScore);

        String riskLevel = determineRiskLevel(riskScore);

        String explanation = buildExplanation(
                riskLevel,
                attendanceScore,
                academicScore,
                assessmentScore,
                trendScore);

        RiskAssessment riskAssessment = new RiskAssessment(
                student,
                semester,
                riskLevel,
                riskScore,
                attendanceScore,
                academicScore,
                assessmentScore,
                trendScore,
                explanation);

        return riskAssessmentRepository.save(riskAssessment);
    }

    private BigDecimal calculateRiskScore(
            BigDecimal attendance,
            BigDecimal academic,
            BigDecimal assessment,
            BigDecimal trend) {

        BigDecimal attendanceRisk =
                BigDecimal.valueOf(100)
                        .subtract(attendance)
                        .multiply(BigDecimal.valueOf(0.25));

        BigDecimal academicRisk =
                BigDecimal.valueOf(100)
                        .subtract(academic)
                        .multiply(BigDecimal.valueOf(0.30));

        BigDecimal assessmentRisk =
                BigDecimal.valueOf(100)
                        .subtract(assessment)
                        .multiply(BigDecimal.valueOf(0.30));

        BigDecimal trendRisk =
                BigDecimal.valueOf(100)
                        .subtract(trend)
                        .multiply(BigDecimal.valueOf(0.15));

        BigDecimal score =
                attendanceRisk
                        .add(academicRisk)
                        .add(assessmentRisk)
                        .add(trendRisk);

        return score.max(BigDecimal.ZERO)
                .min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String determineRiskLevel(BigDecimal riskScore) {

        if (riskScore.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "HIGH";
        }

        if (riskScore.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String buildExplanation(
            String riskLevel,
            BigDecimal attendance,
            BigDecimal academic,
            BigDecimal assessment,
            BigDecimal trend) {

        return String.format(
                "Risk level %s. Attendance: %s, Academic: %s, Assessment: %s, Trend: %s.",
                riskLevel,
                attendance,
                academic,
                assessment,
                trend);
    }
}