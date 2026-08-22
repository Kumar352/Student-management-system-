package com.studentintel.platform.risk;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.dto.RiskResponse;
import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.semester.SemesterRepository;
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;
import com.studentintel.platform.service.RiskService;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private final RiskService riskService;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;

    public RiskController(
            RiskService riskService,
            RiskAssessmentRepository riskAssessmentRepository,
            StudentRepository studentRepository,
            SemesterRepository semesterRepository) {

        this.riskService = riskService;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.studentRepository = studentRepository;
        this.semesterRepository = semesterRepository;
    }

    @GetMapping
    public ResponseEntity<List<RiskResponse>> getAllRiskAssessments() {

        List<RiskResponse> response =
                riskAssessmentRepository.findAll()
                        .stream()
                        .map(RiskResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RiskResponse>> getStudentRisk(
            @PathVariable Long studentId) {

        List<RiskResponse> response =
                riskAssessmentRepository.findByStudentId(studentId)
                        .stream()
                        .map(RiskResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/student/{studentId}/latest")
    public ResponseEntity<RiskResponse> getLatestRisk(
            @PathVariable Long studentId) {

        return riskAssessmentRepository
                .findTopByStudentIdOrderByCalculatedAtDesc(studentId)
                .map(RiskResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate")
    public ResponseEntity<RiskResponse> calculate(
            @RequestParam Long studentId,
            @RequestParam Long semesterId,
            @RequestParam BigDecimal academicScore,
            @RequestParam BigDecimal trendScore) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Semester not found"));

        RiskAssessment risk =
                riskService.calculateRisk(
                        student,
                        semester,
                        academicScore,
                        trendScore);

        return ResponseEntity.ok(
                RiskResponse.from(risk));
    }
}