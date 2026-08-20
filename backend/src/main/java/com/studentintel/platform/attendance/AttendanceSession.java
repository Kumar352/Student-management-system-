package com.studentintel.platform.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.studentintel.platform.course.Course;
import com.studentintel.platform.semester.Semester;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance_sessions")
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_type", nullable = false, length = 30)
    private String sessionType = "LECTURE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected AttendanceSession() {
    }

    public AttendanceSession(
            Course course,
            Semester semester,
            LocalDate sessionDate,
            String sessionType) {

        this.course = course;
        this.semester = semester;
        this.sessionDate = sessionDate;
        this.sessionType = sessionType;
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

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public String getSessionType() {
        return sessionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}