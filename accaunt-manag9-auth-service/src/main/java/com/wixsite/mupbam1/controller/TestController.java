package com.wixsite.mupbam1.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test-db")
    public String testDbConnection() {
        try (Connection conn = DriverManager.getConnection(
            System.getProperty("DB_URL"),
            System.getProperty("DB_USERNAME"),
            System.getProperty("DB_PASSWORD"))) {
            return "Connection is OK!";
        } catch (SQLException e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}

