package com.studentintel.platform.assessmentresult;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.assessment.Assessment;
import com.studentintel.platform.assessment.AssessmentRepository;
import com.studentintel.platform.dto.PerformanceResponse;
import com.studentintel.platform.dto.request.CreateAssessmentResultRequest;
import com.studentintel.platform.performance.PerformanceRecord;
import com.studentintel.platform.service.PerformanceService;
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assessment-results")
public class AssessmentResultWriteController {

    private final AssessmentResultRepository assessmentResultRepository;
    private final AssessmentRepository assessmentRepository;
    private final StudentRepository studentRepository;
    private final PerformanceService performanceService;

    public AssessmentResultWriteController(
            AssessmentResultRepository assessmentResultRepository,
            AssessmentRepository assessmentRepository,
            StudentRepository studentRepository,
            PerformanceService performanceService) {

        this.assessmentResultRepository = assessmentResultRepository;
        this.assessmentRepository = assessmentRepository;
        this.studentRepository = studentRepository;
        this.performanceService = performanceService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PerformanceResponse> createResult(
            @Valid @RequestBody CreateAssessmentResultRequest request) {

        Assessment assessment =
                assessmentRepository.findById(request.assessmentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Assessment not found"));

        Student student =
                studentRepository.findById(request.studentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student not found"));

        AssessmentResult result = new AssessmentResult(
                assessment,
                student,
                request.marksObtained(),
                request.grade());

        assessmentResultRepository.save(result);

        PerformanceRecord performance =
                performanceService.calculatePerformance(
                        student,
                        assessment.getSemester());

        return ResponseEntity.ok(
                PerformanceResponse.from(performance));
    }
}