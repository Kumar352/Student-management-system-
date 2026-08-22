package com.studentintel.platform.student;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.studentintel.platform.dto.StudentResponse;
import com.studentintel.platform.dto.request.CreateStudentRequest;
import com.studentintel.platform.program.Program;
import com.studentintel.platform.program.ProgramRepository;
import com.studentintel.platform.user.User;
import com.studentintel.platform.user.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/students")
public class StudentWriteController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;

    public StudentWriteController(
            StudentRepository studentRepository,
            UserRepository userRepository,
            ProgramRepository programRepository) {

        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.programRepository = programRepository;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody CreateStudentRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Program program = programRepository.findById(request.programId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Program not found"));

        Student student = new Student(
                user,
                request.studentNumber(),
                program,
                request.dateOfBirth(),
                request.admissionYear(),
                request.currentSemester());

        Student saved = studentRepository.save(student);

        return ResponseEntity.ok(
                StudentResponse.from(saved));
    }

    @PutMapping("/{id}/semester")
    public ResponseEntity<StudentResponse> updateCurrentSemester(
            @PathVariable Long id,
            @RequestParam Integer semester) {

        return studentRepository.findById(id)
                .map(student -> {
                    student.setCurrentSemester(semester);

                    Student saved =
                            studentRepository.save(student);

                    return ResponseEntity.ok(
                            StudentResponse.from(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<StudentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return studentRepository.findById(id)
                .map(student -> {
                    student.setStatus(status);

                    Student saved =
                            studentRepository.save(student);

                    return ResponseEntity.ok(
                            StudentResponse.from(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}