package com.studentintel.platform.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentWriteController {

    private final StudentRepository studentRepository;

    public StudentWriteController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(
            @RequestBody Student student) {

        return ResponseEntity.ok(
                studentRepository.save(student));
    }

    @PutMapping("/{id}/semester")
    public ResponseEntity<Student> updateCurrentSemester(
            @PathVariable Long id,
            @RequestParam Integer semester) {

        return studentRepository.findById(id)
                .map(student -> {
                    student.setCurrentSemester(semester);
                    return ResponseEntity.ok(
                            studentRepository.save(student));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Student> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return studentRepository.findById(id)
                .map(student -> {
                    student.setStatus(status);
                    return ResponseEntity.ok(
                            studentRepository.save(student));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}