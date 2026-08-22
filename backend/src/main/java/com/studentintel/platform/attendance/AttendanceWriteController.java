package com.studentintel.platform.attendance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceWriteController {

    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceWriteController(
            AttendanceRecordRepository attendanceRecordRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @PostMapping
    public ResponseEntity<AttendanceRecord> markAttendance(
            @RequestBody AttendanceRecord attendanceRecord) {

        return ResponseEntity.ok(
                attendanceRecordRepository.save(attendanceRecord));
    }
}