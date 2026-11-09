package com.ledgerline.ledgerline.service;

import com.ledgerline.ledgerline.dto.AuthResponse;
import com.ledgerline.ledgerline.dto.LoginRequest;
import com.ledgerline.ledgerline.dto.RegisterRequest;
import com.ledgerline.ledgerline.dto.UserSummaryResponse;
import com.ledgerline.ledgerline.entity.Role;
import com.ledgerline.ledgerline.entity.RoleType;
import com.ledgerline.ledgerline.entity.User;
import com.ledgerline.ledgerline.exception.DuplicateResourceException;
import com.ledgerline.ledgerline.exception.InvalidCredentialsException;
import com.ledgerline.ledgerline.exception.LedgerlineException;
import com.ledgerline.ledgerline.repository.RoleRepository;
import com.ledgerline.ledgerline.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Business service orchestrating user registration, credential authentication, and token revocation.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRevocationService tokenRevocationService;

    public AuthService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationManager authenticationManager,
        TokenRevocationService tokenRevocationService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRevocationService = tokenRevocationService;
    }

    /**
     * Registers a new user.
     * Enforces default role assignment (ROLE_ACCOUNT_HOLDER) to prevent privilege escalation.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("An account with email " + normalizedEmail + " already exists");
        }

        Role defaultRole = roleRepository.findByName(RoleType.ROLE_ACCOUNT_HOLDER)
            .orElseThrow(() -> new LedgerlineException("Default system role ROLE_ACCOUNT_HOLDER is not initialized"));

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(
            normalizedEmail,
            encodedPassword,
            request.firstName().trim(),
            request.lastName().trim()
        );
        user.addRole(defaultRole);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        UserSummaryResponse userSummary = toUserSummary(savedUser);

        return AuthResponse.bearer(token, jwtService.getExpirationMs(), userSummary);
    }

    /**
     * Authenticates user credentials via Spring AuthenticationManager.
     * Uniformly returns InvalidCredentialsException to prevent account enumeration.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);
            UserSummaryResponse userSummary = toUserSummary(user);

            return AuthResponse.bearer(token, jwtService.getExpirationMs(), userSummary);
        } catch (BadCredentialsException | DisabledException | LockedException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Authentication failed");
        }
    }

    /**
     * Logs out the user by blacklisting the active JWT in the revocation table.
     */
    @Transactional
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return;
        }

        try {
            String email = jwtService.extractUsername(token);
            Date expiration = jwtService.extractExpiration(token);
            Instant expiresAt = (expiration != null) ? expiration.toInstant() : Instant.now().plusMillis(jwtService.getExpirationMs());

            tokenRevocationService.revokeToken(token, email != null ? email : "anonymous", expiresAt);
        } catch (Exception ex) {
            // If token parsing fails (already invalid/tampered), treat as already revoked
        }
    }

    private UserSummaryResponse toUserSummary(User user) {
        Set<String> roleNames = user.getRoles().stream()
            .map(Role::getAuthority)
            .collect(Collectors.toSet());

        return new UserSummaryResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            roleNames
        );
    }
}

