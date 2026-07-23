package com.sas.models;

public class StudentDTO {
    private String id;
    private String name;
    private String dept;
    private double gpa;
    private String att; // Attendance formatted as a string (e.g., "71.0%")
    private String risk; // e.g., "High"
    private String score; // e.g., "65/100"

    // Empty Constructor
    public StudentDTO() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String getAtt() { return att; }
    public void setAtt(String att) { this.att = att; }

    public String getRisk() { return risk; }
    public void setRisk(String risk) { this.risk = risk; }

    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }
}