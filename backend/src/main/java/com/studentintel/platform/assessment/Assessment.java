package com.studentintel.platform.assessment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.studentintel.platform.course.Course;
import com.studentintel.platform.semester.Semester;

import jakarta.persistence.*;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "assessment_type", nullable = false, length = 50)
    private String assessmentType;

    @Column(name = "max_marks", nullable = false, precision = 6, scale = 2)
    private BigDecimal maxMarks;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weightage;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Assessment() {
    }

    public Assessment(
            Course course,
            Semester semester,
            String title,
            String assessmentType,
            BigDecimal maxMarks,
            BigDecimal weightage,
            LocalDate assessmentDate) {

        this.course = course;
        this.semester = semester;
        this.title = title;
        this.assessmentType = assessmentType;
        this.maxMarks = maxMarks;
        this.weightage = weightage;
        this.assessmentDate = assessmentDate;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public Semester getSemester() {
        return semester;
    }

    public String getTitle() {
        return title;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public BigDecimal getMaxMarks() {
        return maxMarks;
    }

    public BigDecimal getWeightage() {
        return weightage;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}