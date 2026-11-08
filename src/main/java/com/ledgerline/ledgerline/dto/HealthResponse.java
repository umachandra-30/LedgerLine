package com.ledgerline.ledgerline.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for application health status.
 * Java records provide immutable, concise structures perfect for REST responses.
 */
public record HealthResponse(
    String status,
    String application,
    Instant timestamp,
    Map<String, Object> details
) {}

