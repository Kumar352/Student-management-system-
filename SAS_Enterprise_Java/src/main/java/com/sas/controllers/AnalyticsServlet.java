package com.sas.controllers;

import com.google.gson.Gson;
import com.sas.utils.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/analytics")
public class AnalyticsServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        Map<String, Object> analytics = new HashMap<>();
        Map<String, Integer> riskCounts = new HashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            
            // 1. Fetch Top Dashboard KPIs with COALESCE to prevent NULL errors
            ResultSet rsKpi = conn.prepareStatement(
                "SELECT " +
                "(SELECT COUNT(*) FROM students) as total, " +
                "(SELECT COALESCE(AVG(gpa), 0) FROM academic_records) as avgGpa, " +
                "(SELECT COUNT(*) FROM risk_metrics WHERE risk_tier IN ('High', 'Critical')) as highRisk, " +
                "(SELECT COALESCE(AVG(attendance_percentage), 0) FROM academic_records) as avgAtt"
            ).executeQuery();

            if (rsKpi.next()) {
                analytics.put("totalStudents", rsKpi.getInt("total"));
                analytics.put("avgGpa", rsKpi.getDouble("avgGpa"));
                analytics.put("highRisk", rsKpi.getInt("highRisk"));
                analytics.put("avgAtt", rsKpi.getDouble("avgAtt"));
            }

            // 2. Fetch data for the Chart.js Doughnut Graph
            ResultSet rsRisk = conn.prepareStatement("SELECT risk_tier, COUNT(*) FROM risk_metrics GROUP BY risk_tier").executeQuery();
            int low = 0, med = 0, high = 0, crit = 0;
            
            while (rsRisk.next()) {
                String tier = rsRisk.getString(1);
                int count = rsRisk.getInt(2);
                if("Low".equalsIgnoreCase(tier)) low = count;
                if("Medium".equalsIgnoreCase(tier)) med = count;
                if("High".equalsIgnoreCase(tier)) high = count;
                if("Critical".equalsIgnoreCase(tier)) crit = count;
            }
            
            riskCounts.put("Low", low);
            riskCounts.put("Medium", med);
            riskCounts.put("High", high);
            riskCounts.put("Critical", crit);
            analytics.put("risks", riskCounts);

            PrintWriter out = res.getWriter();
            out.print(gson.toJson(analytics));
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            // If the database fails, send safe default zeros instead of crashing the UI
            analytics.put("totalStudents", 0);
            analytics.put("avgGpa", 0.0);
            analytics.put("highRisk", 0);
            analytics.put("avgAtt", 0.0);
            
            riskCounts.put("Low", 0); riskCounts.put("Medium", 0); 
            riskCounts.put("High", 0); riskCounts.put("Critical", 0);
            analytics.put("risks", riskCounts);
            
            res.setStatus(200); 
            PrintWriter out = res.getWriter();
            out.print(gson.toJson(analytics));
            out.flush();
        }
    }
}