package com.salon.config;

import com.salon.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * SECURITY CONFIGURATION
 * 
 * This is the CENTRAL SECURITY SETUP for the entire application.
 * It defines:
 * 1. Which endpoints are PUBLIC (no login needed)
 * 2. Which endpoints need AUTHENTICATION (valid JWT token)
 * 3. Which endpoints need specific ROLES (ADMIN only, etc.)
 * 4. How passwords are encoded
 * 5. CORS configuration (who can call our APIs)
 * 
 * @Configuration: This class provides beans (objects) to Spring's container
 * @EnableWebSecurity: Activates Spring Security's web protection
 * @EnableMethodSecurity: Allows @PreAuthorize annotations on controller methods
 *   e.g., @PreAuthorize("hasRole('ADMIN')") on admin-only endpoints
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * SECURITY FILTER CHAIN
     * 
     * Defines the security rules for all HTTP endpoints.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF (Cross-Site Request Forgery) protection:
            // Disabled because we use JWT tokens, not sessions/cookies.
            // CSRF is only relevant for browser-based session authentication.
            .csrf(csrf -> csrf.disable())

            // CORS: Allow cross-origin requests (needed for Android/web clients)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // URL-based authorization rules
            .authorizeHttpRequests(auth -> auth
                // PUBLIC endpoints — no authentication required
                .requestMatchers("/api/auth/**").permitAll()          // Login, Register
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll() // API docs
                .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()  // Browse services
                .requestMatchers(HttpMethod.GET, "/api/artists/**").permitAll()   // Browse artists

                // ADMIN-only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // All other endpoints require authentication (any role)
                .anyRequest().authenticated()
            )

            // Session management: STATELESS because we use JWT, not sessions
            // Each request is independently authenticated via the JWT token
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Add our JWT filter BEFORE Spring's default username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * PASSWORD ENCODER
     * 
     * BCrypt is a one-way hashing algorithm designed for passwords:
     * - Automatically includes a random "salt" (prevents rainbow table attacks)
     * - Intentionally SLOW (prevents brute-force attacks)
     * - Strength 10 means 2^10 = 1024 iterations
     * 
     * "password123" → "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     * Same password, different hash every time (due to random salt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AUTHENTICATION MANAGER
     * 
     * Spring Security uses this to authenticate users during login.
     * It compares the provided password with the stored hash using our PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * CORS CONFIGURATION
     * 
     * CORS (Cross-Origin Resource Sharing):
     * By default, browsers block requests from one domain to another for security.
     * Since our Android app / web frontend runs on a different origin than our API,
     * we need to explicitly allow cross-origin requests.
     * 
     * In production, replace "*" with your actual frontend domain!
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));              // Allow all origins (for dev)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));              // Allow all headers
        configuration.setExposedHeaders(List.of("Authorization"));  // Expose JWT header to client

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
