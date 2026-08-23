package com.studentintel.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.studentintel.platform.assessment.Assessment;
import com.studentintel.platform.assessmentresult.AssessmentResult;
import com.studentintel.platform.assessmentresult.AssessmentResultRepository;
import com.studentintel.platform.performance.PerformanceRecord;
import com.studentintel.platform.performance.PerformanceRecordRepository;
import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.student.Student;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @Mock
    private AssessmentResultRepository assessmentResultRepository;

    @Mock
    private PerformanceRecordRepository performanceRecordRepository;

    @Mock
    private Student student;

    @Mock
    private Semester semester;

    @Mock
    private Assessment assessment;

    @Mock
    private AssessmentResult assessmentResult;

    @Mock
    private PerformanceRecord existingRecord;

    private PerformanceService performanceService;

    @BeforeEach
    void setUp() {
        performanceService = new PerformanceService(
                assessmentResultRepository,
                performanceRecordRepository);
    }

    @Test
    void calculatePerformanceCreatesRecordWhenNoneExists() {

        when(student.getId()).thenReturn(1L);
        when(semester.getId()).thenReturn(1L);

        when(assessmentResultRepository.findByStudentId(1L))
                .thenReturn(List.of(assessmentResult));

        when(assessmentResult.getAssessment())
                .thenReturn(assessment);

        when(assessment.getSemester())
                .thenReturn(semester);

        when(assessment.getMaxMarks())
                .thenReturn(BigDecimal.valueOf(100));

        when(assessment.getWeightage())
                .thenReturn(BigDecimal.valueOf(100));

        when(assessmentResult.getMarksObtained())
                .thenReturn(BigDecimal.valueOf(85));

        when(performanceRecordRepository
                .findByStudentIdAndSemesterId(1L, 1L))
                .thenReturn(Optional.empty());

        when(performanceRecordRepository.save(any(PerformanceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PerformanceRecord result =
                performanceService.calculatePerformance(
                        student,
                        semester);

        assertEquals(
                BigDecimal.valueOf(9),
                result.getGpa());

        assertEquals(
                BigDecimal.valueOf(9),
                result.getCgpa());

        assertEquals(
                "EXCELLENT",
                result.getAcademicStatus());

        verify(performanceRecordRepository)
                .save(any(PerformanceRecord.class));
    }

    @Test
    void calculatePerformanceUpdatesExistingRecord() {

        when(student.getId()).thenReturn(1L);
        when(semester.getId()).thenReturn(1L);

        when(assessmentResultRepository.findByStudentId(1L))
                .thenReturn(List.of(assessmentResult));

        when(assessmentResult.getAssessment())
                .thenReturn(assessment);

        when(assessment.getSemester())
                .thenReturn(semester);

        when(assessment.getMaxMarks())
                .thenReturn(BigDecimal.valueOf(100));

        when(assessment.getWeightage())
                .thenReturn(BigDecimal.valueOf(100));

        when(assessmentResult.getMarksObtained())
                .thenReturn(BigDecimal.valueOf(72));

        when(performanceRecordRepository
                .findByStudentIdAndSemesterId(1L, 1L))
                .thenReturn(Optional.of(existingRecord));

        when(performanceRecordRepository.save(existingRecord))
                .thenReturn(existingRecord);

        PerformanceRecord result =
                performanceService.calculatePerformance(
                        student,
                        semester);

        assertEquals(existingRecord, result);

        verify(existingRecord)
                .setGpa(BigDecimal.valueOf(8));

        verify(existingRecord)
                .setCgpa(BigDecimal.valueOf(8));

        verify(existingRecord)
                .setAcademicStatus("EXCELLENT");

        verify(performanceRecordRepository)
                .save(existingRecord);
    }

    @Test
    void emptyResultsProduceZeroGpaAndAtRiskStatus() {

        when(student.getId()).thenReturn(1L);
        when(semester.getId()).thenReturn(1L);

        when(assessmentResultRepository.findByStudentId(1L))
                .thenReturn(List.of());

        when(performanceRecordRepository
                .findByStudentIdAndSemesterId(1L, 1L))
                .thenReturn(Optional.empty());

        when(performanceRecordRepository.save(any(PerformanceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PerformanceRecord result =
                performanceService.calculatePerformance(
                        student,
                        semester);

        assertEquals(
                BigDecimal.ZERO,
                result.getGpa());

        assertEquals(
                BigDecimal.ZERO,
                result.getCgpa());

        assertEquals(
                "AT_RISK",
                result.getAcademicStatus());
    }

    @Test
    void weightedAssessmentsAreCalculatedCorrectly() {

        when(student.getId()).thenReturn(1L);
        when(semester.getId()).thenReturn(1L);

        AssessmentResult first = org.mockito.Mockito.mock(
                AssessmentResult.class);

        AssessmentResult second = org.mockito.Mockito.mock(
                AssessmentResult.class);

        Assessment firstAssessment = org.mockito.Mockito.mock(
                Assessment.class);

        Assessment secondAssessment = org.mockito.Mockito.mock(
                Assessment.class);

        when(first.getAssessment())
                .thenReturn(firstAssessment);

        when(second.getAssessment())
                .thenReturn(secondAssessment);

        when(firstAssessment.getSemester())
                .thenReturn(semester);

        when(secondAssessment.getSemester())
                .thenReturn(semester);

        when(firstAssessment.getMaxMarks())
                .thenReturn(BigDecimal.valueOf(100));

        when(secondAssessment.getMaxMarks())
                .thenReturn(BigDecimal.valueOf(50));

        when(firstAssessment.getWeightage())
                .thenReturn(BigDecimal.valueOf(40));

        when(secondAssessment.getWeightage())
                .thenReturn(BigDecimal.valueOf(60));

        when(first.getMarksObtained())
                .thenReturn(BigDecimal.valueOf(80));

        when(second.getMarksObtained())
                .thenReturn(BigDecimal.valueOf(40));

        when(assessmentResultRepository.findByStudentId(1L))
                .thenReturn(List.of(first, second));

        BigDecimal score =
                performanceService.calculateAssessmentScore(
                        student,
                        semester);

        /*
         * First:
         * 80 / 100 * 100 = 80
         * 80 * 40% = 32
         *
         * Second:
         * 40 / 50 * 100 = 80
         * 80 * 60% = 48
         *
         * Total = 80
         */

        assertEquals(
                BigDecimal.valueOf(80).setScale(2),
                score);
    }

    @Test
    void zeroMaxMarksAssessmentIsIgnored() {

        when(student.getId()).thenReturn(1L);
        when(semester.getId()).thenReturn(1L);

        when(assessmentResultRepository.findByStudentId(1L))
                .thenReturn(List.of(assessmentResult));

        when(assessmentResult.getAssessment())
                .thenReturn(assessment);

        when(assessment.getSemester())
                .thenReturn(semester);

        when(assessment.getMaxMarks())
                .thenReturn(BigDecimal.ZERO);

        BigDecimal score =
                performanceService.calculateAssessmentScore(
                        student,
                        semester);

        assertEquals(
                BigDecimal.ZERO.setScale(2),
                score);
    }

    @Test
    void gpaNinetyOrAboveIsTen() {

        PerformanceRecord result =
                createPerformanceForPercentage(
                        BigDecimal.valueOf(95));

        assertEquals(
                BigDecimal.TEN,
                result.getGpa());

        assertEquals(
                "EXCELLENT",
                result.getAcademicStatus());
    }

    @Test
    void gpaEightyTo89IsNine() {

        PerformanceRecord result =
                createPerformanceForPercentage(
                        BigDecimal.valueOf(85));

        assertEquals(
                BigDecimal.valueOf(9),
                result.getGpa());

        assertEquals(
                "EXCELLENT",
                result.getAcademicStatus());
    }

    @Test
    void gpaSeventyTo79IsEight() {

        PerformanceRecord result =
                createPerformanceForPercentage(
                        BigDecimal.valueOf(75));

        assertEquals(
                BigDecimal.valueOf(8),
                result.getGpa());

        assertEquals(
                "EXCELLENT",
                result.getAcademicStatus());
    }

    @Test
    void gpaSixtyTo69IsSeven() {

        PerformanceRecord result =
                createPerformanceForPercentage(
                        BigDecimal.valueOf(65));

        assertEquals(
                BigDecimal.valueOf(7),
                result.getGpa());

        assertEquals(
                "GOOD",
                result.getAcademicStatus());
    }

    @Test
    void gpaFiftyTo59IsSix() {

        PerformanceRecord result =
                createPerformanceForPercentage(
                        BigDecimal.valueOf(55));

        assertEquals(
                BigDecimal.valueOf(6),
                result.getGpa());

        assertEquals(
                "GOOD",
                result.getAcademicStatus());
    }

    @Test
    void gpaFortyTo49IsFive() {

        PerformanceRecord result =
                createPerformanceForPercentage(
                        BigDecimal.valueOf(45));

        assertEquals(
                BigDecimal.valueOf(5),
                result.getGpa());

        assertEquals(
                "AVERAGE",
                result.getAcademicStatus());
    }

    @Test
    void gpaBelowFortyIsZeroAndAtRisk() {

        PerformanceRecord result =
                createPerformanceForPercentage(
                        BigDecimal.valueOf(35));

        assertEquals(
                BigDecimal.ZERO,
                result.getGpa());

        assertEquals(
                "AT_RISK",
                result.getAcademicStatus());
    }

    private PerformanceRecord createPerformanceForPercentage(
            BigDecimal percentage) {

        when(student.getId()).thenReturn(1L);
        when(semester.getId()).thenReturn(1L);

        when(assessmentResultRepository.findByStudentId(1L))
                .thenReturn(List.of(assessmentResult));

        when(assessmentResult.getAssessment())
                .thenReturn(assessment);

        when(assessment.getSemester())
                .thenReturn(semester);

        when(assessment.getMaxMarks())
                .thenReturn(BigDecimal.valueOf(100));

        when(assessment.getWeightage())
                .thenReturn(BigDecimal.valueOf(100));

        when(assessmentResult.getMarksObtained())
                .thenReturn(percentage);

        when(performanceRecordRepository
                .findByStudentIdAndSemesterId(1L, 1L))
                .thenReturn(Optional.empty());

        when(performanceRecordRepository.save(any(PerformanceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        return performanceService.calculatePerformance(
                student,
                semester);
    }
}