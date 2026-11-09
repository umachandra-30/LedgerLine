package com.ledgerline.ledgerline.service;

import com.ledgerline.ledgerline.entity.RevokedToken;
import com.ledgerline.ledgerline.repository.RevokedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Service managing token revocation and logout invalidation.
 * Stores cryptographically secure SHA-256 hashes of invalidated tokens.
 */
@Service
public class TokenRevocationService {

    private final RevokedTokenRepository revokedTokenRepository;

    public TokenRevocationService(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    /**
     * Revokes a JWT token by storing its SHA-256 hash and expiration timestamp.
     */
    @Transactional
    public void revokeToken(String token, String userEmail, Instant expiresAt) {
        String tokenHash = hashToken(token);
        if (!revokedTokenRepository.existsByTokenHash(tokenHash)) {
            RevokedToken revokedToken = new RevokedToken(tokenHash, userEmail, expiresAt);
            revokedTokenRepository.save(revokedToken);
        }
    }

    /**
     * Verifies if a token has been revoked / logged out.
     */
    @Transactional(readOnly = true)
    public boolean isTokenRevoked(String token) {
        String tokenHash = hashToken(token);
        return revokedTokenRepository.existsByTokenHash(tokenHash);
    }

    /**
     * Removes expired tokens from the revocation table to prevent table bloat.
     */
    @Transactional
    public void cleanupExpiredTokens() {
        revokedTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }

    /**
     * Hashes a token using SHA-256 to ensure raw tokens are never exposed in database records.
     */
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }
}

