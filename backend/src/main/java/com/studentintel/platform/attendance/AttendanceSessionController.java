package com.studentintel.platform.attendance;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance/sessions")
public class AttendanceSessionController {

    private final AttendanceSessionRepository attendanceSessionRepository;

    public AttendanceSessionController(
            AttendanceSessionRepository attendanceSessionRepository) {
        this.attendanceSessionRepository = attendanceSessionRepository;
    }

    @GetMapping
    public ResponseEntity<List<AttendanceSession>> getAllSessions() {
        return ResponseEntity.ok(attendanceSessionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceSession> getSession(
            @PathVariable Long id) {

        return attendanceSessionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}