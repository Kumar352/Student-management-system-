package com.sas.dao;

import com.sas.models.StudentDTO;
import com.sas.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // This performs a complex JOIN across all 4 tables to build the dashboard view
    public static List<StudentDTO> getAllStudentsForDashboard() {
        List<StudentDTO> list = new ArrayList<>();
        
        String sql = "SELECT s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS full_name, " +
                     "d.dept_code, a.gpa, a.attendance_percentage, " +
                     "r.calculated_score, r.risk_tier " +
                     "FROM students s " +
                     "JOIN departments d ON s.department_id = d.department_id " +
                     "JOIN academic_records a ON s.student_id = a.student_id " +
                     "JOIN risk_metrics r ON s.student_id = r.student_id " +
                     "ORDER BY r.calculated_score DESC"; // Show highest risk first

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StudentDTO dto = new StudentDTO();
                dto.setId(rs.getString("student_id"));
                dto.setName(rs.getString("full_name"));
                dto.setDept(rs.getString("dept_code"));
                dto.setGpa(rs.getDouble("gpa"));
                
                // Format the numbers for the UI
                dto.setAtt(rs.getDouble("attendance_percentage") + "%");
                dto.setRisk(rs.getString("risk_tier"));
                dto.setScore(rs.getInt("calculated_score") + "/100");
                
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}