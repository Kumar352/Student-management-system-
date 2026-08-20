package com.studentintel.platform.student;

import java.time.LocalDate;

import com.studentintel.platform.program.Program;
import com.studentintel.platform.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "student_number", nullable = false, unique = true, length = 30)
    private String studentNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "admission_year", nullable = false)
    private Integer admissionYear;

    @Column(name = "current_semester", nullable = false)
    private Integer currentSemester;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    protected Student() {
    }

    public Student(
            User user,
            String studentNumber,
            Program program,
            LocalDate dateOfBirth,
            Integer admissionYear,
            Integer currentSemester) {

        this.user = user;
        this.studentNumber = studentNumber;
        this.program = program;
        this.dateOfBirth = dateOfBirth;
        this.admissionYear = admissionYear;
        this.currentSemester = currentSemester;
        this.status = "ACTIVE";
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public Program getProgram() {
        return program;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getAdmissionYear() {
        return admissionYear;
    }

    public Integer getCurrentSemester() {
        return currentSemester;
    }

    public String getStatus() {
        return status;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public void setCurrentSemester(Integer currentSemester) {
        this.currentSemester = currentSemester;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}