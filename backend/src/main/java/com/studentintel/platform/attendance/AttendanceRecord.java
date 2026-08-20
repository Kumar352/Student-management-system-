package com.studentintel.platform.attendance;

import java.time.LocalDateTime;

import com.studentintel.platform.student.Student;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private AttendanceSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "marked_at", nullable = false, updatable = false)
    private LocalDateTime markedAt;

    protected AttendanceRecord() {
    }

    public AttendanceRecord(
            AttendanceSession session,
            Student student,
            String status) {

        this.session = session;
        this.student = student;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        markedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AttendanceSession getSession() {
        return session;
    }

    public Student getStudent() {
        return student;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}