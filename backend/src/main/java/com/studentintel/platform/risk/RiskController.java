package com.studentintel.platform.risk;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<RiskAssessment>> getAllRiskAssessments() {
        return ResponseEntity.ok(riskAssessmentRepository.findAll());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RiskAssessment>> getStudentRisk(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                riskAssessmentRepository.findByStudentId(studentId));
    }

    @PostMapping("/calculate")
    public ResponseEntity<RiskAssessment> calculate(
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

        return ResponseEntity.ok(
                riskService.calculateRisk(
                        student,
                        semester,
                        academicScore,
                        trendScore));
    }
}