package com.ledgerline.ledgerline.service;

import com.ledgerline.ledgerline.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Service managing JWT generation, cryptographic signing, parsing, and claim validation.
 */
@Service
public class JwtService {

    private final String secretKey;
    private final long jwtExpirationMs;
    private final TokenRevocationService tokenRevocationService;

    public JwtService(
        @Value("${ledgerline.security.jwt.secret}") String secretKey,
        @Value("${ledgerline.security.jwt.expiration-ms:86400000}") long jwtExpirationMs,
        TokenRevocationService tokenRevocationService
    ) {
        this.secretKey = secretKey;
        this.jwtExpirationMs = jwtExpirationMs;
        this.tokenRevocationService = tokenRevocationService;
    }

    /**
     * Generates a signed JWT with user identity, roles, and expiration.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        extraClaims.put("roles", roles);

        if (userDetails instanceof User user) {
            extraClaims.put("userId", user.getId());
            extraClaims.put("fullName", user.getFirstName() + " " + user.getLastName());
        }

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtExpirationMs);

        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Extracts the subject (username/email) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the list of roles embedded in the token.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    /**
     * Resolves an arbitrary claim using a functional claims resolver.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates whether a token is valid, matches the user, has not expired,
     * and has not been revoked.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean usernameMatches = username != null && username.equalsIgnoreCase(userDetails.getUsername());
            boolean notExpired = !isTokenExpired(token);
            boolean notRevoked = !tokenRevocationService.isTokenRevoked(token);

            return usernameMatches && notExpired && notRevoked;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Checks if the token has expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception ex) {
            return true;
        }
    }

    /**
     * Extracts all claims using the verified signing key.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Constructs the cryptographic HMAC-SHA256 signing key from configured secret.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = HexFormat.of().parseHex(secretKey);
        } catch (Exception hexEx) {
            try {
                keyBytes = Decoders.BASE64.decode(secretKey);
            } catch (Exception b64Ex) {
                keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}

