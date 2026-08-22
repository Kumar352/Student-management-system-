package com.studentintel.platform.student;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.performance.PerformanceRecord;
import com.studentintel.platform.performance.PerformanceRecordRepository;
import com.studentintel.platform.risk.RiskAssessment;
import com.studentintel.platform.risk.RiskAssessmentRepository;
import com.studentintel.platform.attendance.AttendanceRecord;
import com.studentintel.platform.attendance.AttendanceRecordRepository;

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

    @GetMapping("/{studentId}/dashboard/performance")
    public ResponseEntity<List<PerformanceRecord>> getPerformance(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                performanceRecordRepository.findByStudentId(studentId));
    }

    @GetMapping("/{studentId}/dashboard/attendance")
    public ResponseEntity<List<AttendanceRecord>> getAttendance(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                attendanceRecordRepository.findByStudentId(studentId));
    }

    @GetMapping("/{studentId}/dashboard/risk")
    public ResponseEntity<List<RiskAssessment>> getRisk(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                riskAssessmentRepository.findByStudentId(studentId));
    }
}