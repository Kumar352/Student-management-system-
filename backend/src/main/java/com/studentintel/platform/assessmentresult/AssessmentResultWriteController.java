package com.studentintel.platform.assessmentresult;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.assessment.Assessment;
import com.studentintel.platform.assessment.AssessmentRepository;
import com.studentintel.platform.performance.PerformanceRecord;
import com.studentintel.platform.service.PerformanceService;
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;

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
    public ResponseEntity<AssessmentResult> createResult(
            @RequestBody AssessmentResult result) {

        AssessmentResult saved =
                assessmentResultRepository.save(result);

        Assessment assessment = saved.getAssessment();
        Student student = saved.getStudent();

        PerformanceRecord performance =
                performanceService.calculatePerformance(
                        student,
                        assessment.getSemester());

        return ResponseEntity.ok(saved);
    }
}