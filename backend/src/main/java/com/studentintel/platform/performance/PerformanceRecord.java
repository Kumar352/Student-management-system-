package com.studentintel.platform.performance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.student.Student;

import jakarta.persistence.*;

@Entity
@Table(name = "performance_records")
public class PerformanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(precision = 4, scale = 2)
    private BigDecimal gpa;

    @Column(precision = 4, scale = 2)
    private BigDecimal cgpa;

    @Column(name = "credits_attempted", precision = 5, scale = 2)
    private BigDecimal creditsAttempted;

    @Column(name = "credits_earned", precision = 5, scale = 2)
    private BigDecimal creditsEarned;

    @Column(name = "academic_status", nullable = false, length = 30)
    private String academicStatus;

    @Column(name = "calculated_at", nullable = false, updatable = false)
    private LocalDateTime calculatedAt;

    protected PerformanceRecord() {
    }

    public PerformanceRecord(
            Student student,
            Semester semester,
            BigDecimal gpa,
            BigDecimal cgpa,
            BigDecimal creditsAttempted,
            BigDecimal creditsEarned,
            String academicStatus) {

        this.student = student;
        this.semester = semester;
        this.gpa = gpa;
        this.cgpa = cgpa;
        this.creditsAttempted = creditsAttempted;
        this.creditsEarned = creditsEarned;
        this.academicStatus = academicStatus;
    }

    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Semester getSemester() {
        return semester;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public BigDecimal getCgpa() {
        return cgpa;
    }

    public BigDecimal getCreditsAttempted() {
        return creditsAttempted;
    }

    public BigDecimal getCreditsEarned() {
        return creditsEarned;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
public void setGpa(BigDecimal gpa) {
    this.gpa = gpa;
}

public void setCgpa(BigDecimal cgpa) {
    this.cgpa = cgpa;
}

public void setAcademicStatus(String academicStatus) {
    this.academicStatus = academicStatus;
}
}