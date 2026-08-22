package com.studentintel.platform.assessmentresult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment-results")
public class AssessmentResultWriteController {

    private final AssessmentResultRepository assessmentResultRepository;

    public AssessmentResultWriteController(
            AssessmentResultRepository assessmentResultRepository) {
        this.assessmentResultRepository = assessmentResultRepository;
    }

    @PostMapping
    public ResponseEntity<AssessmentResult> createResult(
            @RequestBody AssessmentResult result) {

        return ResponseEntity.ok(
                assessmentResultRepository.save(result));
    }
}