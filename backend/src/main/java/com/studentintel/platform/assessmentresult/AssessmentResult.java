package com.studentintel.platform.assessmentresult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.studentintel.platform.assessment.Assessment;
import com.studentintel.platform.student.Student;

import jakarta.persistence.*;

@Entity
@Table(name = "assessment_results")
public class AssessmentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "marks_obtained", nullable = false, precision = 6, scale = 2)
    private BigDecimal marksObtained;

    @Column(length = 5)
    private String grade;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected AssessmentResult() {
    }

    public AssessmentResult(
            Assessment assessment,
            Student student,
            BigDecimal marksObtained,
            String grade) {

        this.assessment = assessment;
        this.student = student;
        this.marksObtained = marksObtained;
        this.grade = grade;
    }

    @PrePersist
    protected void onCreate() {
        if (gradedAt == null) {
            gradedAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public Student getStudent() {
        return student;
    }

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public String getGrade() {
        return grade;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}