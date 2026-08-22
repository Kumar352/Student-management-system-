package com.studentintel.platform.student;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.attendance.AttendanceRecord;
import com.studentintel.platform.attendance.AttendanceRecordRepository;
import com.studentintel.platform.dto.PerformanceResponse;
import com.studentintel.platform.dto.RiskResponse;
import com.studentintel.platform.performance.PerformanceRecordRepository;
import com.studentintel.platform.risk.RiskAssessmentRepository;

@RestController
@RequestMapping("/api/students")
public class StudentDashboardController {

    private final PerformanceRecordRepository performanceRecordRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;

    public StudentDashboardController(
            PerformanceRecordRepository performanceRecordRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            RiskAssessmentRepository riskAssessmentRepository) {

        this.performanceRecordRepository = performanceRecordRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/{studentId}/dashboard/performance")
    public ResponseEntity<List<PerformanceResponse>> getPerformance(
            @PathVariable Long studentId) {

        List<PerformanceResponse> response =
                performanceRecordRepository.findByStudentId(studentId)
                        .stream()
                        .map(PerformanceResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/{studentId}/dashboard/attendance")
    public ResponseEntity<List<AttendanceRecord>> getAttendance(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                attendanceRecordRepository.findByStudentId(studentId));
    }

    @PreAuthorize("@studentSecurityService.canAccessStudent(#studentId, authentication)")
    @GetMapping("/{studentId}/dashboard/risk")
    public ResponseEntity<List<RiskResponse>> getRisk(
            @PathVariable Long studentId) {

        List<RiskResponse> response =
                riskAssessmentRepository.findByStudentId(studentId)
                        .stream()
                        .map(RiskResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }
}