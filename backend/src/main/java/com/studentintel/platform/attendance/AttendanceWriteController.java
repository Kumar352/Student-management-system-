package com.studentintel.platform.attendance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.dto.request.MarkAttendanceRequest;
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceWriteController {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final StudentRepository studentRepository;

    public AttendanceWriteController(
            AttendanceRecordRepository attendanceRecordRepository,
            AttendanceSessionRepository attendanceSessionRepository,
            StudentRepository studentRepository) {

        this.attendanceRecordRepository = attendanceRecordRepository;
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public ResponseEntity<AttendanceRecord> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request) {

        AttendanceSession session =
                attendanceSessionRepository.findById(request.sessionId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Attendance session not found"));

        Student student =
                studentRepository.findById(request.studentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student not found"));

        AttendanceRecord attendanceRecord =
                new AttendanceRecord(
                        session,
                        student,
                        request.status());

        return ResponseEntity.ok(
                attendanceRecordRepository.save(attendanceRecord));
    }
}