package com.ledgerline.ledgerline.controller;

import com.ledgerline.ledgerline.dto.HealthResponse;
import com.ledgerline.ledgerline.service.DatabaseHealthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Health check REST controller providing system observability and database status.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final DatabaseHealthService databaseHealthService;

    public HealthController(DatabaseHealthService databaseHealthService) {
        this.databaseHealthService = databaseHealthService;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> getHealth() {
        Map<String, Object> dbDetails = databaseHealthService.checkDatabaseHealth();
        boolean isDbUp = "UP".equals(dbDetails.get("status"));

        HealthResponse response = new HealthResponse(
            isDbUp ? "UP" : "DEGRADED",
            "LedgerLine Financial Core",
            Instant.now(),
            Map.of("database", dbDetails)
        );

        HttpStatus status = isDbUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(response);
    }
}

