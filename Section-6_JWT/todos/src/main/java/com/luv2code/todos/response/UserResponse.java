package com.luv2code.todos.response;

import com.luv2code.todos.entity.Authority;

import java.util.List;

public class UserResponse {

    private long id;

    private String email;

    private String fullName;

    private List<Authority> authorities;

    public UserResponse() {
    }

    public UserResponse(long id, String email, String fullName, List<Authority> authorities) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.authorities = authorities;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
