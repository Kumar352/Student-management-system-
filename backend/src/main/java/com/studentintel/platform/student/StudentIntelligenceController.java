package com.studentintel.platform.student;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.dto.PerformanceResponse;
import com.studentintel.platform.dto.RiskResponse;
import com.studentintel.platform.performance.PerformanceRecordRepository;
import com.studentintel.platform.risk.RiskAssessmentRepository;

@RestController
@RequestMapping("/api/students")
public class StudentIntelligenceController {

    private final StudentRepository studentRepository;
    private final PerformanceRecordRepository performanceRecordRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;

    public StudentIntelligenceController(
            StudentRepository studentRepository,
            PerformanceRecordRepository performanceRecordRepository,
            RiskAssessmentRepository riskAssessmentRepository) {

        this.studentRepository = studentRepository;
        this.performanceRecordRepository = performanceRecordRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/{studentId}/intelligence")
    public ResponseEntity<Map<String, Object>> getIntelligence(
            @PathVariable Long studentId) {

        if (!studentRepository.existsById(studentId)) {
            return ResponseEntity.notFound().build();
        }

        PerformanceResponse performance =
                performanceRecordRepository
                        .findTopByStudentIdOrderByCalculatedAtDesc(studentId)
                        .map(PerformanceResponse::from)
                        .orElse(null);

        RiskResponse risk =
                riskAssessmentRepository
                        .findTopByStudentIdOrderByCalculatedAtDesc(studentId)
                        .map(RiskResponse::from)
                        .orElse(null);

        Map<String, Object> response = new HashMap<>();

        response.put("studentId", studentId);
        response.put("performance", performance);
        response.put("risk", risk);

        return ResponseEntity.ok(response);
    }
}