package com.salon.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT UTILITY CLASS
 * 
 * WHAT IS JWT (JSON Web Token)?
 * A JWT is a compact, self-contained token that securely transmits information between parties.
 * 
 * Structure: xxxxx.yyyyy.zzzzz (three parts separated by dots)
 * 1. HEADER: Algorithm used (e.g., HS256) + token type
 * 2. PAYLOAD: The actual data (user email, role, expiration)
 * 3. SIGNATURE: Verifies the token hasn't been tampered with
 * 
 * FLOW:
 * 1. User logs in with email/password
 * 2. Server verifies credentials
 * 3. Server generates a JWT token with user info and signs it with a SECRET KEY
 * 4. Client stores the token (SharedPreferences in Android)
 * 5. Client sends token in EVERY request: "Authorization: Bearer <token>"
 * 6. Server validates the token signature and extracts user info
 * 
 * WHY JWT INSTEAD OF SESSIONS?
 * - Stateless: Server doesn't store session data (scales better)
 * - Perfect for mobile apps: No cookies needed
 * - Self-contained: Token carries all needed info
 */
@Component
public class JwtUtil {

    /**
     * @Value reads from application.properties:
     * jwt.secret=MySuperSecretKey...
     * jwt.expiration=86400000
     */
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Creates the signing key from our secret string.
     * HMAC-SHA requires a key of at least 256 bits (32 bytes).
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * GENERATE TOKEN
     * 
     * Creates a new JWT token for an authenticated user.
     * The token contains: email (subject), role, issued time, expiration time.
     * 
     * Example token payload:
     * {
     *   "sub": "john@example.com",
     *   "role": "CUSTOMER",
     *   "iat": 1705334400,      (issued at)
     *   "exp": 1705420800       (expires at = iat + 24 hours)
     * }
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)                                     // Add custom claims (role)
                .subject(email)                                      // Set subject (email)
                .issuedAt(new Date())                                // Token creation time
                .expiration(new Date(System.currentTimeMillis() + expiration))  // Expiry time
                .signWith(getSigningKey())                           // Sign with our secret key
                .compact();                                          // Build the token string
    }

    /** Extract email (subject) from token */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Extract role from token */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /** Extract expiration date from token */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Check if token has expired */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * VALIDATE TOKEN
     * 
     * Checks two things:
     * 1. The email in the token matches the UserDetails' username
     * 2. The token hasn't expired
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /** Generic method to extract any claim using a function */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** Parse the token and extract all claims */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
