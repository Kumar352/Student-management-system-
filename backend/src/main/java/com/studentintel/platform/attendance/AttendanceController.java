package com.studentintel.platform.attendance;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceController(
            AttendanceRecordRepository attendanceRecordRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @GetMapping
    public ResponseEntity<List<AttendanceRecord>> getAllAttendance() {
        return ResponseEntity.ok(attendanceRecordRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceRecord> getAttendance(
            @PathVariable Long id) {

        return attendanceRecordRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceRecord>> getStudentAttendance(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                attendanceRecordRepository.findByStudentId(studentId));
    }
}