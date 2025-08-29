package com.insurance.backoffice.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and extraction of claims.
 * Clean Code: Single responsibility for JWT operations.
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKey}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;
    
    /**
     * Generates a JWT token for the given user details.
     * Clean Code: Descriptive method name that reveals intention.
     * 
     * @param userDetails the user details to generate token for
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    /**
     * Creates a JWT token with specified claims and subject.
     * Clean Code: Private helper method with clear purpose.
     * 
     * @param claims additional claims to include in token
     * @param subject the subject (username) for the token
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Extracts username from JWT token.
     * Clean Code: Intention-revealing method name.
     * 
     * @param token JWT token
     * @return username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extracts expiration date from JWT token.
     * Clean Code: Clear method purpose.
     * 
     * @param token JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extracts a specific claim from JWT token.
     * Clean Code: Generic method for claim extraction.
     * 
     * @param token JWT token
     * @param claimsResolver function to extract specific claim
     * @param <T> type of claim
     * @return extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extracts all claims from JWT token.
     * Clean Code: Private helper method.
     * 
     * @param token JWT token
     * @return all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Checks if JWT token is expired.
     * Clean Code: Boolean method with clear purpose.
     * 
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Validates JWT token against user details.
     * Clean Code: Validation method with clear business logic.
     * 
     * @param token JWT token
     * @param userDetails user details to validate against
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Gets the signing key for JWT operations.
     * Clean Code: Private helper method for key generation.
     * 
     * @return signing key
     */
    private SecretKey getSigningKey() {
        // Use UTF-8 encoding and ensure proper key size for HS512
        byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        // HS512 requires at least 512 bits (64 bytes)
        if (keyBytes.length < 64) {
            // Pad the key to 64 bytes if it's too short
            byte[] paddedKey = new byte[64];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 64));
            return Keys.hmacShaKeyFor(paddedKey);
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
}