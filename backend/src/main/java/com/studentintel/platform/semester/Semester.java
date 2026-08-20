package com.studentintel.platform.semester;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "semesters")
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(nullable = false, length = 30)
    private String term;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 30)
    private String status = "PLANNED";

    protected Semester() {
    }

    public Semester(
            String name,
            String academicYear,
            String term,
            LocalDate startDate,
            LocalDate endDate) {

        this.name = name;
        this.academicYear = academicYear;
        this.term = term;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "PLANNED";
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getTerm() {
        return term;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}