package com.studentintel.platform.performance;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.dto.PerformanceResponse;
import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.semester.SemesterRepository;
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;
import com.studentintel.platform.service.PerformanceService;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final PerformanceRecordRepository performanceRecordRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;

    public PerformanceController(
            PerformanceService performanceService,
            PerformanceRecordRepository performanceRecordRepository,
            StudentRepository studentRepository,
            SemesterRepository semesterRepository) {

        this.performanceService = performanceService;
        this.performanceRecordRepository = performanceRecordRepository;
        this.studentRepository = studentRepository;
        this.semesterRepository = semesterRepository;
    }

    @GetMapping
    public ResponseEntity<List<PerformanceResponse>> getAllPerformance() {

        List<PerformanceResponse> response =
                performanceRecordRepository.findAll()
                        .stream()
                        .map(PerformanceResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PerformanceResponse>> getStudentPerformance(
            @PathVariable Long studentId) {

        List<PerformanceResponse> response =
                performanceRecordRepository.findByStudentId(studentId)
                        .stream()
                        .map(PerformanceResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/student/{studentId}/latest")
    public ResponseEntity<PerformanceResponse> getLatestPerformance(
            @PathVariable Long studentId) {

        return performanceRecordRepository
                .findTopByStudentIdOrderByCalculatedAtDesc(studentId)
                .map(PerformanceResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate")
    public ResponseEntity<PerformanceResponse> calculate(
            @RequestParam Long studentId,
            @RequestParam Long semesterId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Semester not found"));

        PerformanceRecord record =
                performanceService.calculatePerformance(
                        student,
                        semester);

        return ResponseEntity.ok(
                PerformanceResponse.from(record));
    }
}
