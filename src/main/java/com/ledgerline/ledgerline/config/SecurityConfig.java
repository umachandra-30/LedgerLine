package com.ledgerline.ledgerline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Base security configuration for Phase 1.
 * 
 * Allows unrestricted access to health-check endpoints (/actuator/**, /api/v1/health)
 * so system observability can be verified without authentication friction.
 * All other routes remain protected.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**", "/api/v1/health").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

