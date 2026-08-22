package com.studentintel.platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers(
                        "/actuator/health",
                        "/error"
                ).permitAll()

                // Administrative APIs
                .requestMatchers(
                        "/api/users/**",
                        "/api/roles/**"
                ).hasAuthority("ADMIN")

                // Student management
                .requestMatchers(
                        "/api/students/**"
                ).hasAnyAuthority(
                        "STUDENT",
                        "FACULTY",
                        "ADMIN"
                )

                // Academic structure
                .requestMatchers(
                        "/api/courses/**",
                        "/api/programs/**",
                        "/api/semesters/**",
                        "/api/assessments/**"
                ).hasAnyAuthority(
                        "STUDENT",
                        "FACULTY",
                        "ADMIN"
                )

                // Department and faculty information
                .requestMatchers(
                        "/api/departments/**",
                        "/api/faculty/**"
                ).hasAnyAuthority(
                        "FACULTY",
                        "ADMIN"
                )

                // Academic operations
                .requestMatchers(
                        "/api/assessment-results/**",
                        "/api/attendance/**",
                        "/api/enrollments/**",
                        "/api/performance/**",
                        "/api/risk/**"
                ).hasAnyAuthority(
                        "STUDENT",
                        "FACULTY",
                        "ADMIN"
                )

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .httpBasic(httpBasic -> {});

        return http.build();
    }
}