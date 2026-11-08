package com.ledgerline.ledgerline.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Service responsible for testing and verifying direct PostgreSQL database connectivity.
 * Implements a lightweight JDBC validation check without overhead.
 */
@Service
public class DatabaseHealthService {

    private final DataSource dataSource;

    public DatabaseHealthService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, Object> checkDatabaseHealth() {
        Map<String, Object> details = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            // Test connection validity with a 2-second timeout
            boolean valid = connection.isValid(2);
            DatabaseMetaData metaData = connection.getMetaData();

            details.put("database", metaData.getDatabaseProductName());
            details.put("version", metaData.getDatabaseProductVersion());
            details.put("connected", valid);
            details.put("status", valid ? "UP" : "DOWN");
        } catch (Exception ex) {
            details.put("status", "DOWN");
            details.put("error", ex.getMessage());
        }
        return details;
    }
}

