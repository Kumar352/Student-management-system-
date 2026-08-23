package com.studentintel.platform.risk;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.studentintel.platform.exception.GlobalExceptionHandler;
import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.semester.SemesterRepository;
import com.studentintel.platform.service.RiskService;
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;

@ExtendWith(MockitoExtension.class)
class RiskControllerTest {

    @Mock
    private RiskService riskService;

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private Student student;

    @Mock
    private Semester semester;

    @Mock
    private RiskAssessment riskAssessment;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        RiskController controller =
                new RiskController(
                        riskService,
                        riskAssessmentRepository,
                        studentRepository,
                        semesterRepository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private void configureRiskResponseMock() {

        when(student.getId())
                .thenReturn(1L);

        when(semester.getId())
                .thenReturn(1L);

        when(riskAssessment.getId())
                .thenReturn(1L);

        when(riskAssessment.getStudent())
                .thenReturn(student);

        when(riskAssessment.getSemester())
                .thenReturn(semester);

        when(riskAssessment.getRiskLevel())
                .thenReturn("MEDIUM");

        when(riskAssessment.getRiskScore())
                .thenReturn(BigDecimal.valueOf(50));

        when(riskAssessment.getAttendanceScore())
                .thenReturn(BigDecimal.valueOf(75));

        when(riskAssessment.getAcademicScore())
                .thenReturn(BigDecimal.valueOf(70));

        when(riskAssessment.getAssessmentScore())
                .thenReturn(BigDecimal.valueOf(72));

        when(riskAssessment.getTrendScore())
                .thenReturn(BigDecimal.valueOf(68));

        when(riskAssessment.getExplanation())
                .thenReturn("Risk level MEDIUM.");

        when(riskAssessment.getCalculatedAt())
                .thenReturn(LocalDateTime.now());
    }

    @Test
    void getAllRiskAssessmentsReturnsOk()
            throws Exception {

        when(riskAssessmentRepository.findAll())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/api/risk"))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentRiskReturnsOk()
            throws Exception {

        when(riskAssessmentRepository.findByStudentId(1L))
                .thenReturn(List.of());

        mockMvc.perform(
                get("/api/risk/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getLatestRiskReturnsOkWhenPresent()
            throws Exception {

        configureRiskResponseMock();

        when(riskAssessmentRepository
                .findTopByStudentIdOrderByCalculatedAtDesc(1L))
                .thenReturn(Optional.of(riskAssessment));

        mockMvc.perform(
                get("/api/risk/student/1/latest"))
                .andExpect(status().isOk());
    }

    @Test
    void getLatestRiskReturnsNotFoundWhenMissing()
            throws Exception {

        when(riskAssessmentRepository
                .findTopByStudentIdOrderByCalculatedAtDesc(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                get("/api/risk/student/999/latest"))
                .andExpect(status().isNotFound());
    }

    @Test
    void calculateRiskReturnsOk()
            throws Exception {

        configureRiskResponseMock();

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(semesterRepository.findById(1L))
                .thenReturn(Optional.of(semester));

        when(riskService.calculateRisk(
                any(Student.class),
                any(Semester.class),
                any(BigDecimal.class),
                any(BigDecimal.class)))
                .thenReturn(riskAssessment);

        mockMvc.perform(
                post("/api/risk/calculate")
                        .param("studentId", "1")
                        .param("semesterId", "1")
                        .param("academicScore", "80")
                        .param("trendScore", "85"))
                .andExpect(status().isOk());
    }

    @Test
    void calculateRiskReturnsBadRequestWhenStudentMissing()
            throws Exception {

        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                post("/api/risk/calculate")
                        .param("studentId", "999")
                        .param("semesterId", "1")
                        .param("academicScore", "80")
                        .param("trendScore", "85"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateRiskReturnsBadRequestWhenSemesterMissing()
            throws Exception {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(semesterRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                post("/api/risk/calculate")
                        .param("studentId", "1")
                        .param("semesterId", "999")
                        .param("academicScore", "80")
                        .param("trendScore", "85"))
                .andExpect(status().isBadRequest());
    }
}