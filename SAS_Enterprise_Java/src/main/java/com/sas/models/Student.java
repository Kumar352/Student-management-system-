package com.sas.models;

public class Student {
    private int id;
    private String name;
    private String registrationNo;
    private String department;
    private double gpa;
    private int riskMetric;

    // Default constructor required for Gson
    public Student() {}

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getRegistrationNo() { return registrationNo; }
    public String getDepartment() { return department; }
    public double getGpa() { return gpa; }
    public int getRiskMetric() { return riskMetric; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    public void setDepartment(String department) { this.department = department; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public void setRiskMetric(int riskMetric) { this.riskMetric = riskMetric; }
}