package com.luv2code.todos.entity;

import jakarta.persistence.Embeddable;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

@Embeddable
// Mark this class is independent
// Mark this class no need ID
// Mark this class is other's component class
public class Authority implements GrantedAuthority {
    private String authority;

    public Authority() {
    }

    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
