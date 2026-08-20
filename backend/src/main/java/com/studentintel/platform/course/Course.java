package com.studentintel.platform.course;

import com.studentintel.platform.department.Department;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "course_code", nullable = false, unique = true, length = 30)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 150)
    private String courseName;

    @Column(nullable = false, precision = 3, scale = 1)
    private java.math.BigDecimal credits;

    @Column(name = "semester_number", nullable = false)
    private Integer semesterNumber;

    @Column(name = "course_type", nullable = false, length = 30)
    private String courseType;

    protected Course() {
    }

    public Course(
            Department department,
            String courseCode,
            String courseName,
            java.math.BigDecimal credits,
            Integer semesterNumber,
            String courseType) {

        this.department = department;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.semesterNumber = semesterNumber;
        this.courseType = courseType;
    }

    public Long getId() {
        return id;
    }

    public Department getDepartment() {
        return department;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public java.math.BigDecimal getCredits() {
        return credits;
    }

    public Integer getSemesterNumber() {
        return semesterNumber;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCredits(java.math.BigDecimal credits) {
        this.credits = credits;
    }

    public void setSemesterNumber(Integer semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }
}