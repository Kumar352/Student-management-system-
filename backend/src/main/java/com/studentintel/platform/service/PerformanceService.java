package com.studentintel.platform.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentintel.platform.assessment.Assessment;
import com.studentintel.platform.assessmentresult.AssessmentResult;
import com.studentintel.platform.assessmentresult.AssessmentResultRepository;
import com.studentintel.platform.performance.PerformanceRecord;
import com.studentintel.platform.performance.PerformanceRecordRepository;
import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.student.Student;

@Service
public class PerformanceService {

    private final AssessmentResultRepository assessmentResultRepository;
    private final PerformanceRecordRepository performanceRecordRepository;

    public PerformanceService(
            AssessmentResultRepository assessmentResultRepository,
            PerformanceRecordRepository performanceRecordRepository) {

        this.assessmentResultRepository = assessmentResultRepository;
        this.performanceRecordRepository = performanceRecordRepository;
    }

    @Transactional
    public PerformanceRecord calculatePerformance(
            Student student,
            Semester semester) {

        List<AssessmentResult> results =
                assessmentResultRepository.findByStudentId(student.getId())
                        .stream()
                        .filter(result ->
                                result.getAssessment()
                                        .getSemester()
                                        .getId()
                                        .equals(semester.getId()))
                        .toList();

        BigDecimal percentage = calculateWeightedPercentage(results);
        BigDecimal gpa = percentageToGpa(percentage);

        PerformanceRecord record =
                performanceRecordRepository
                        .findByStudentIdAndSemesterId(
                                student.getId(),
                                semester.getId())
                        .orElse(new PerformanceRecord(
                                student,
                                semester,
                                gpa,
                                gpa,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                determineAcademicStatus(gpa)));

        record.setGpa(gpa);
        record.setCgpa(gpa);
        record.setAcademicStatus(determineAcademicStatus(gpa));

        return performanceRecordRepository.save(record);
    }

    private BigDecimal calculateWeightedPercentage(
            List<AssessmentResult> results) {

        if (results.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (AssessmentResult result : results) {

            Assessment assessment = result.getAssessment();

            if (assessment.getMaxMarks() == null ||
                    assessment.getMaxMarks().signum() == 0) {
                continue;
            }

            BigDecimal scorePercentage =
                    result.getMarksObtained()
                            .divide(
                                    assessment.getMaxMarks(),
                                    4,
                                    RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

            BigDecimal weighted =
                    scorePercentage
                            .multiply(assessment.getWeightage())
                            .divide(
                                    BigDecimal.valueOf(100),
                                    4,
                                    RoundingMode.HALF_UP);

            total = total.add(weighted);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentageToGpa(BigDecimal percentage) {

        double value = percentage.doubleValue();

        if (value >= 90) return BigDecimal.TEN;
        if (value >= 80) return BigDecimal.valueOf(9);
        if (value >= 70) return BigDecimal.valueOf(8);
        if (value >= 60) return BigDecimal.valueOf(7);
        if (value >= 50) return BigDecimal.valueOf(6);
        if (value >= 40) return BigDecimal.valueOf(5);

        return BigDecimal.ZERO;
    }

    private String determineAcademicStatus(BigDecimal gpa) {

        if (gpa.compareTo(BigDecimal.valueOf(8)) >= 0) {
            return "EXCELLENT";
        }

        if (gpa.compareTo(BigDecimal.valueOf(6)) >= 0) {
            return "GOOD";
        }

        if (gpa.compareTo(BigDecimal.valueOf(5)) >= 0) {
            return "AVERAGE";
        }

        return "AT_RISK";
    }
public BigDecimal calculateAssessmentScore(
        Student student,
        Semester semester) {

    List<AssessmentResult> results =
            assessmentResultRepository.findByStudentId(student.getId())
                    .stream()
                    .filter(result ->
                            result.getAssessment()
                                    .getSemester()
                                    .getId()
                                    .equals(semester.getId()))
                    .toList();

    return calculateWeightedPercentage(results);
}
}