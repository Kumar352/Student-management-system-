package com.studentintel.platform.performance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.semester.SemesterRepository;
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;
import com.studentintel.platform.service.PerformanceService;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;

    public PerformanceController(
            PerformanceService performanceService,
            StudentRepository studentRepository,
            SemesterRepository semesterRepository) {

        this.performanceService = performanceService;
        this.studentRepository = studentRepository;
        this.semesterRepository = semesterRepository;
    }

    @PostMapping("/calculate")
    public ResponseEntity<PerformanceRecord> calculate(
            @RequestParam Long studentId,
            @RequestParam Long semesterId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Semester not found"));

        return ResponseEntity.ok(
                performanceService.calculatePerformance(
                        student,
                        semester));
    }
}