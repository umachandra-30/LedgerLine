package com.ledgerline.ledgerline.dto;

/**
 * Authentication response returned upon successful registration or login.
 */
public record AuthResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    UserSummaryResponse user
) {
    public static AuthResponse bearer(String accessToken, long expiresIn, UserSummaryResponse user) {
        return new AuthResponse(accessToken, "Bearer", expiresIn, user);
    }
}

