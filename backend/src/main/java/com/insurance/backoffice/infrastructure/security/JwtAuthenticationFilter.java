package com.insurance.backoffice.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that processes JWT tokens in HTTP requests.
 * Extends OncePerRequestFilter to ensure single execution per request.
 * Clean Code: Single responsibility for JWT token processing.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    
    /**
     * Processes each HTTP request to extract and validate JWT tokens.
     * Clean Code: Main filter logic with clear flow and error handling.
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        // Extract JWT token from Authorization header
        if (isValidAuthorizationHeader(authorizationHeader)) {
            jwt = authorizationHeader.substring(7); // Remove "Bearer " prefix
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.warn("JWT token extraction failed: " + e.getMessage());
            }
        }
        
        // Validate token and set authentication
        if (shouldAuthenticateUser(username)) {
            authenticateUser(request, username, jwt);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Checks if Authorization header is valid and contains Bearer token.
     * Clean Code: Validation method with clear purpose.
     * 
     * @param authorizationHeader the Authorization header value
     * @return true if header is valid, false otherwise
     */
    private boolean isValidAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }
    
    /**
     * Determines if user should be authenticated based on username and current context.
     * Clean Code: Boolean method with clear business logic.
     * 
     * @param username extracted username from token
     * @return true if user should be authenticated, false otherwise
     */
    private boolean shouldAuthenticateUser(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }
    
    /**
     * Authenticates user by validating JWT token and setting security context.
     * Clean Code: Authentication logic separated into focused method.
     * 
     * @param request HTTP request
     * @param username extracted username
     * @param jwt JWT token
     */
    private void authenticateUser(HttpServletRequest request, String username, String jwt) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.warn("User authentication failed: " + e.getMessage());
        }
    }
}