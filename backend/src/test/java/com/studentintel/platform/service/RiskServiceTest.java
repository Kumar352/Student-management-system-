package com.studentintel.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.studentintel.platform.risk.RiskAssessment;
import com.studentintel.platform.risk.RiskAssessmentRepository;
import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.student.Student;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private PerformanceService performanceService;

    @Mock
    private Student student;

    @Mock
    private Semester semester;

    @Mock
    private RiskAssessment savedRiskAssessment;

    private RiskService riskService;

    @BeforeEach
    void setUp() {
        riskService = new RiskService(
                riskAssessmentRepository,
                attendanceService,
                performanceService);
    }

    @Test
    void lowRiskScenarioIsSavedCorrectly() {

        when(student.getId()).thenReturn(1L);

        when(attendanceService.calculateAttendancePercentage(1L))
                .thenReturn(BigDecimal.valueOf(95));

        when(performanceService.calculateAssessmentScore(
                student, semester))
                .thenReturn(BigDecimal.valueOf(90));

        when(riskAssessmentRepository.save(any(RiskAssessment.class)))
                .thenReturn(savedRiskAssessment);

        RiskAssessment result =
                riskService.calculateRisk(
                        student,
                        semester,
                        BigDecimal.valueOf(90),
                        BigDecimal.valueOf(90));

        assertEquals(savedRiskAssessment, result);

        verify(riskAssessmentRepository)
                .save(any(RiskAssessment.class));
    }

    @Test
    void mediumRiskScenarioProducesMediumRisk() {

        when(student.getId()).thenReturn(1L);

        when(attendanceService.calculateAttendancePercentage(1L))
                .thenReturn(BigDecimal.valueOf(50));

        when(performanceService.calculateAssessmentScore(
                student, semester))
                .thenReturn(BigDecimal.valueOf(50));

        when(riskAssessmentRepository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RiskAssessment result =
                riskService.calculateRisk(
                        student,
                        semester,
                        BigDecimal.valueOf(50),
                        BigDecimal.valueOf(50));

        assertEquals("MEDIUM", result.getRiskLevel());
    }

    @Test
    void highRiskScenarioProducesHighRisk() {

        when(student.getId()).thenReturn(1L);

        when(attendanceService.calculateAttendancePercentage(1L))
                .thenReturn(BigDecimal.valueOf(20));

        when(performanceService.calculateAssessmentScore(
                student, semester))
                .thenReturn(BigDecimal.valueOf(20));

        when(riskAssessmentRepository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RiskAssessment result =
                riskService.calculateRisk(
                        student,
                        semester,
                        BigDecimal.valueOf(20),
                        BigDecimal.valueOf(20));

        assertEquals("HIGH", result.getRiskLevel());
    }

    @Test
    void perfectScoresProduceZeroRiskAndLowRiskLevel() {

        when(student.getId()).thenReturn(1L);

        when(attendanceService.calculateAttendancePercentage(1L))
                .thenReturn(BigDecimal.valueOf(100));

        when(performanceService.calculateAssessmentScore(
                student, semester))
                .thenReturn(BigDecimal.valueOf(100));

        when(riskAssessmentRepository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RiskAssessment result =
                riskService.calculateRisk(
                        student,
                        semester,
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(100));

        assertEquals(
                BigDecimal.ZERO.setScale(2),
                result.getRiskScore());

        assertEquals("LOW", result.getRiskLevel());
    }

    @Test
    void zeroScoresProduceMaximumRisk() {

        when(student.getId()).thenReturn(1L);

        when(attendanceService.calculateAttendancePercentage(1L))
                .thenReturn(BigDecimal.ZERO);

        when(performanceService.calculateAssessmentScore(
                student, semester))
                .thenReturn(BigDecimal.ZERO);

        when(riskAssessmentRepository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RiskAssessment result =
                riskService.calculateRisk(
                        student,
                        semester,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        assertEquals(
                BigDecimal.valueOf(100).setScale(2),
                result.getRiskScore());

        assertEquals("HIGH", result.getRiskLevel());
    }

    @Test
    void riskCalculationUsesAttendanceAndPerformanceServices() {

        when(student.getId()).thenReturn(25L);

        when(attendanceService.calculateAttendancePercentage(25L))
                .thenReturn(BigDecimal.valueOf(80));

        when(performanceService.calculateAssessmentScore(
                student, semester))
                .thenReturn(BigDecimal.valueOf(85));

        when(riskAssessmentRepository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        riskService.calculateRisk(
                student,
                semester,
                BigDecimal.valueOf(82),
                BigDecimal.valueOf(88));

        verify(attendanceService)
                .calculateAttendancePercentage(25L);

        verify(performanceService)
                .calculateAssessmentScore(student, semester);
    }

    @Test
    void riskExplanationContainsAllInputFactors() {

        when(student.getId()).thenReturn(1L);

        when(attendanceService.calculateAttendancePercentage(1L))
                .thenReturn(BigDecimal.valueOf(60));

        when(performanceService.calculateAssessmentScore(
                student, semester))
                .thenReturn(BigDecimal.valueOf(65));

        when(riskAssessmentRepository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RiskAssessment result =
                riskService.calculateRisk(
                        student,
                        semester,
                        BigDecimal.valueOf(70),
                        BigDecimal.valueOf(75));

        String explanation = result.getExplanation();

        assertEquals(true, explanation.contains("Attendance: 60"));
        assertEquals(true, explanation.contains("Academic: 70"));
        assertEquals(true, explanation.contains("Assessment: 65"));
        assertEquals(true, explanation.contains("Trend: 75"));
    }

    @Test
    void eightyPercentFactorsProduceLowRisk() {

        when(student.getId()).thenReturn(1L);

        when(attendanceService.calculateAttendancePercentage(1L))
                .thenReturn(BigDecimal.valueOf(80));

        when(performanceService.calculateAssessmentScore(
                student, semester))
                .thenReturn(BigDecimal.valueOf(80));

        when(riskAssessmentRepository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RiskAssessment result =
                riskService.calculateRisk(
                        student,
                        semester,
                        BigDecimal.valueOf(80),
                        BigDecimal.valueOf(80));

        assertEquals(
                BigDecimal.valueOf(20).setScale(2),
                result.getRiskScore());

        assertEquals("LOW", result.getRiskLevel());
    }
}