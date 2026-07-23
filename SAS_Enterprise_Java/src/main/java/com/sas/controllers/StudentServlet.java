package com.sas.controllers;

import com.google.gson.Gson;
import com.sas.dao.StudentDAO;
import com.sas.models.StudentDTO;
import com.sas.utils.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/students")
public class StudentServlet extends HttpServlet {
    
    private Gson gson = new Gson();

    // --- AI RISK CALCULATOR ---
    private int calculateAIRisk(double gpa, int departmentId, Connection conn) {
        double risk = (10.0 - gpa) * 2.5; 

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT AVG(a.gpa) FROM academic_records a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "WHERE s.department_id = ?")) {
            
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double deptAvg = rs.getDouble(1);
                if (deptAvg > 0) {
                    if (gpa < deptAvg) risk += 3.0; 
                    if (gpa < deptAvg - 1.5) risk += 4.0; 
                }
            }
        } catch (Exception e) { 
            // Silent fail, use base risk 
        }

        if (gpa < 6.0) risk += 5.0;

        int finalRisk = (int) Math.round(risk);
        return Math.min(100, Math.max(1, finalRisk)); // Adjusted for 1-100 scale 
    }

    private String getRiskTier(int score) {
        if (score >= 76) return "Critical";
        if (score >= 51) return "High";
        if (score >= 26) return "Medium";
        return "Low";
    }

    // --- READ (GET) - Powered by the DAO ---
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        
        List<StudentDTO> students = StudentDAO.getAllStudentsForDashboard();
        
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("data", students);
        jsonResponse.put("total", students.size());
        
        PrintWriter out = res.getWriter();
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }

    // --- CREATE (POST) - Multi-Table Transaction ---
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Connection conn = null;
        try {
            BufferedReader reader = req.getReader();
            
            // Reusing StudentDTO to catch the incoming JSON from the frontend
            StudentDTO payload = gson.fromJson(reader, StudentDTO.class);
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // START TRANSACTION

            // 1. Find Department ID
            int deptId = 1; // Default
            try (PreparedStatement deptStmt = conn.prepareStatement("SELECT department_id FROM departments WHERE dept_code = ?")) {
                deptStmt.setString(1, payload.getDept());
                ResultSet rs = deptStmt.executeQuery();
                if (rs.next()) deptId = rs.getInt(1);
            }

            // 2. Split Name
            String[] names = payload.getName().split(" ", 2);
            String firstName = names[0];
            String lastName = names.length > 1 ? names[1] : "";
            String email = payload.getId().toLowerCase() + "@srm.edu";

            // 3. Insert into Core Students Table
            try (PreparedStatement sStmt = conn.prepareStatement(
                    "INSERT INTO students (student_id, first_name, last_name, email, enrollment_year, department_id) VALUES (?, ?, ?, ?, ?, ?)")) {
                sStmt.setString(1, payload.getId());
                sStmt.setString(2, firstName);
                sStmt.setString(3, lastName);
                sStmt.setString(4, email);
                sStmt.setInt(5, 2024);
                sStmt.setInt(6, deptId);
                sStmt.executeUpdate();
            }

            // 4. Insert into Academic Records
            try (PreparedStatement aStmt = conn.prepareStatement(
                    "INSERT INTO academic_records (student_id, semester, gpa, attendance_percentage, failed_subjects) VALUES (?, ?, ?, ?, ?)")) {
                aStmt.setString(1, payload.getId());
                aStmt.setInt(2, 3);
                aStmt.setDouble(3, payload.getGpa());
                aStmt.setDouble(4, 75.0); // Default attendance for new records
                aStmt.setInt(5, 0);
                aStmt.executeUpdate();
            }

            // 5. Calculate and Insert Risk Metrics
            int riskScore = calculateAIRisk(payload.getGpa(), deptId, conn);
            String riskTier = getRiskTier(riskScore);
            
            try (PreparedStatement rStmt = conn.prepareStatement(
                    "INSERT INTO risk_metrics (student_id, calculated_score, risk_tier) VALUES (?, ?, ?)")) {
                rStmt.setString(1, payload.getId());
                rStmt.setInt(2, riskScore);
                rStmt.setString(3, riskTier);
                rStmt.executeUpdate();
            }

            conn.commit(); // SAVE TRANSACTION
            res.setStatus(HttpServletResponse.SC_CREATED);

        } catch (Exception e) { 
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {} // ROLLBACK ON ERROR
            }
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ex) {}
            }
        }
    }

    // --- DELETE (DELETE) ---
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM students WHERE student_id=?")) {
            
            // Because of 'ON DELETE CASCADE' in MySQL, this automatically deletes their academic_records and risk_metrics too!
            stmt.setString(1, req.getParameter("regNo")); 
            stmt.executeUpdate();
            res.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        }
    }
}