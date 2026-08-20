package com.studentintel.platform.faculty;

import java.time.LocalDate;

import com.studentintel.platform.department.Department;
import com.studentintel.platform.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "faculty")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "employee_number", nullable = false, unique = true, length = 30)
    private String employeeNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, length = 100)
    private String designation;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    protected Faculty() {
    }

    public Faculty(
            User user,
            String employeeNumber,
            Department department,
            String designation,
            LocalDate joiningDate) {

        this.user = user;
        this.employeeNumber = employeeNumber;
        this.department = department;
        this.designation = designation;
        this.joiningDate = joiningDate;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public Department getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public String getStatus() {
        return status;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}