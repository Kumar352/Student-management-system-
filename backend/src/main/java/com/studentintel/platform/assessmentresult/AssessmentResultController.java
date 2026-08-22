package com.studentintel.platform.assessmentresult;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment-results")
public class AssessmentResultController {

    private final AssessmentResultRepository assessmentResultRepository;

    public AssessmentResultController(
            AssessmentResultRepository assessmentResultRepository) {

        this.assessmentResultRepository = assessmentResultRepository;
    }

    @GetMapping
    public ResponseEntity<List<AssessmentResult>> getAllResults() {

        return ResponseEntity.ok(
                assessmentResultRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResult> getResult(
            @PathVariable Long id) {

        return assessmentResultRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AssessmentResult>> getStudentResults(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                assessmentResultRepository.findByStudentId(studentId));
    }
}