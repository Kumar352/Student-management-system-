package com.studentintel.platform.assessmentresult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.studentintel.platform.assessment.Assessment;
import com.studentintel.platform.assessment.AssessmentRepository;
import com.studentintel.platform.exception.GlobalExceptionHandler;
import com.studentintel.platform.performance.PerformanceRecord;
import com.studentintel.platform.service.PerformanceService;
import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;

@ExtendWith(MockitoExtension.class)
class AssessmentResultWriteControllerTest {

    @Mock
    private AssessmentResultRepository assessmentResultRepository;

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PerformanceService performanceService;

    @Mock
    private Assessment assessment;

    @Mock
    private Student student;

    @Mock
    private Semester semester;

    @Mock
    private PerformanceRecord performanceRecord;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        AssessmentResultWriteController controller =
                new AssessmentResultWriteController(
                        assessmentResultRepository,
                        assessmentRepository,
                        studentRepository,
                        performanceService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingAssessmentIdIsRejected() throws Exception {

        String json = """
                {
                    "studentId": 1,
                    "marksObtained": 80,
                    "grade": "A"
                }
                """;

        mockMvc.perform(
                post("/api/assessment-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingStudentIdIsRejected() throws Exception {

        String json = """
                {
                    "assessmentId": 1,
                    "marksObtained": 80,
                    "grade": "A"
                }
                """;

        mockMvc.perform(
                post("/api/assessment-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingMarksIsRejected() throws Exception {

        String json = """
                {
                    "assessmentId": 1,
                    "studentId": 1,
                    "grade": "A"
                }
                """;

        mockMvc.perform(
                post("/api/assessment-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativeMarksAreRejected() throws Exception {

        String json = """
                {
                    "assessmentId": 1,
                    "studentId": 1,
                    "marksObtained": -1,
                    "grade": "F"
                }
                """;

        mockMvc.perform(
                post("/api/assessment-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assessmentNotFoundReturnsBadRequest() throws Exception {

        when(assessmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        String json = """
                {
                    "assessmentId": 999,
                    "studentId": 1,
                    "marksObtained": 80,
                    "grade": "A"
                }
                """;

        mockMvc.perform(
                post("/api/assessment-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void studentNotFoundReturnsBadRequest() throws Exception {

        when(assessmentRepository.findById(1L))
                .thenReturn(Optional.of(assessment));

        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        String json = """
                {
                    "assessmentId": 1,
                    "studentId": 999,
                    "marksObtained": 80,
                    "grade": "A"
                }
                """;

        mockMvc.perform(
                post("/api/assessment-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validAssessmentResultTriggersPerformanceCalculation()
            throws Exception {

        when(assessmentRepository.findById(1L))
                .thenReturn(Optional.of(assessment));

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(student.getId())
                .thenReturn(1L);

        when(assessment.getSemester())
                .thenReturn(semester);

        when(semester.getId())
                .thenReturn(1L);

        when(performanceService.calculatePerformance(
                student,
                semester))
                .thenReturn(performanceRecord);

        when(performanceRecord.getStudent())
                .thenReturn(student);

        when(performanceRecord.getSemester())
                .thenReturn(semester);

        when(performanceRecord.getId())
                .thenReturn(1L);

        when(performanceRecord.getGpa())
                .thenReturn(BigDecimal.valueOf(8.0));

        when(performanceRecord.getCgpa())
                .thenReturn(BigDecimal.valueOf(8.0));

        when(performanceRecord.getCreditsAttempted())
                .thenReturn(BigDecimal.valueOf(20));

        when(performanceRecord.getCreditsEarned())
                .thenReturn(BigDecimal.valueOf(20));

        when(performanceRecord.getAcademicStatus())
                .thenReturn("EXCELLENT");

        when(performanceRecord.getCalculatedAt())
                .thenReturn(LocalDateTime.now());

        String json = """
                {
                    "assessmentId": 1,
                    "studentId": 1,
                    "marksObtained": 80,
                    "grade": "A"
                }
                """;

        mockMvc.perform(
                post("/api/assessment-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(assessmentResultRepository)
                .save(any(AssessmentResult.class));

        verify(performanceService)
                .calculatePerformance(
                        student,
                        semester);
    }
}