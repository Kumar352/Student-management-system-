package com.sas.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_analytics";
    private static final String USER = "root";
    private static final String PASS = "admin1234"; 

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("DATABASE CONNECTION FAILED!");
            e.printStackTrace();
            return null;
        }
    }
}