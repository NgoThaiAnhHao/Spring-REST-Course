package com.luv2code.todos.config;

import com.luv2code.todos.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Get Authorization Header
        // Frontend usually send "Authorization: Bearer eyJhbGciOi..."
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Check Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            // endpoint public => can go inside
            // endpoint protected => blocked by Spring => 401
            filterChain.doFilter(request, response);
            return;
        }

        // Get token and Username
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // Check if not logged in
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // CHECK TOKEN:
            //   Correct signature
            //   Not yet expired
            //   Correct username/email
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Create Authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Save request details include : IP, session id, request info
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Add user to SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }

        // The request continues to the controller.
        filterChain.doFilter(request, response);
    }
}
