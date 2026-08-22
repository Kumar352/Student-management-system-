package com.studentintel.platform.semester;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    private final SemesterRepository semesterRepository;

    public SemesterController(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    @GetMapping
    public ResponseEntity<List<Semester>> getAllSemesters() {
        return ResponseEntity.ok(semesterRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Semester> getSemester(@PathVariable Long id) {
        return semesterRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}