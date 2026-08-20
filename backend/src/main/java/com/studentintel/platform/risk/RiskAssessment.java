package com.studentintel.platform.risk;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.studentintel.platform.semester.Semester;
import com.studentintel.platform.student.Student;

import jakarta.persistence.*;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "attendance_score", precision = 5, scale = 2)
    private BigDecimal attendanceScore;

    @Column(name = "academic_score", precision = 5, scale = 2)
    private BigDecimal academicScore;

    @Column(name = "assessment_score", precision = 5, scale = 2)
    private BigDecimal assessmentScore;

    @Column(name = "trend_score", precision = 5, scale = 2)
    private BigDecimal trendScore;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "calculated_at", nullable = false, updatable = false)
    private LocalDateTime calculatedAt;

    protected RiskAssessment() {
    }

    public RiskAssessment(
            Student student,
            Semester semester,
            String riskLevel,
            BigDecimal riskScore,
            BigDecimal attendanceScore,
            BigDecimal academicScore,
            BigDecimal assessmentScore,
            BigDecimal trendScore,
            String explanation) {

        this.student = student;
        this.semester = semester;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.attendanceScore = attendanceScore;
        this.academicScore = academicScore;
        this.assessmentScore = assessmentScore;
        this.trendScore = trendScore;
        this.explanation = explanation;
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

    public String getRiskLevel() {
        return riskLevel;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public BigDecimal getAttendanceScore() {
        return attendanceScore;
    }

    public BigDecimal getAcademicScore() {
        return academicScore;
    }

    public BigDecimal getAssessmentScore() {
        return assessmentScore;
    }

    public BigDecimal getTrendScore() {
        return trendScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
}