package com.salon.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT AUTHENTICATION FILTER
 * 
 * This filter runs on EVERY incoming HTTP request, BEFORE it reaches the controller.
 * 
 * Think of it as a SECURITY GUARD at the door:
 * 1. "Do you have an ID badge (JWT token)?" → Check Authorization header
 * 2. "Is your ID badge valid?" → Verify the JWT signature and expiration
 * 3. "Who are you?" → Extract user info from the token
 * 4. "OK, you can enter." → Set the user in Spring Security context
 * 
 * If the token is missing or invalid, the request continues WITHOUT authentication.
 * Spring Security will then decide whether the endpoint requires authentication
 * and return 401 Unauthorized if it does.
 * 
 * OncePerRequestFilter ensures this filter runs EXACTLY ONCE per request
 * (even if the request is forwarded internally).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Get the Authorization header
        // Expected format: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIi..."
        final String authHeader = request.getHeader("Authorization");

        // Step 2: If no header or not a Bearer token, skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the token (remove "Bearer " prefix)
        final String jwt = authHeader.substring(7);

        try {
            // Step 4: Extract email from the token
            final String email = jwtUtil.extractEmail(jwt);

            // Step 5: If we got an email AND the user isn't already authenticated in this request
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 6: Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Step 7: Validate the token (check signature + expiration + email match)
                if (jwtUtil.validateToken(jwt, userDetails)) {

                    // Step 8: Create an authentication token and set it in Security Context
                    // This tells Spring Security: "This user is authenticated for this request"
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // If token is malformed, expired, or invalid — just continue without authentication.
            // The security config will handle returning 401 for protected endpoints.
            logger.error("JWT Authentication failed: " + e.getMessage());
        }

        // Continue the filter chain (pass request to next filter or controller)
        filterChain.doFilter(request, response);
    }
}
