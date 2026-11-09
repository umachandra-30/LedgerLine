package com.ledgerline.ledgerline.dto;

import java.util.Set;

/**
 * Safe public representation of a user.
 * Strictly excludes sensitive fields like password hashes.
 */
public record UserSummaryResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    Set<String> roles
) {}

