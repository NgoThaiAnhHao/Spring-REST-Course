package com.luv2code.todos.util;

import com.luv2code.todos.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FindAuthenticatedUserImpl implements FindAuthenticatedUser {
    @Override
    public User getAuthenticatedUser() {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if unauthenticated, throw exception
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Access Denied! Authentication required!");
        }

        return (User) authentication.getPrincipal();
    }
}
