package com.luv2code.todos.service;

import com.luv2code.todos.entity.User;
import com.luv2code.todos.request.PasswordUpdateRequest;
import com.luv2code.todos.response.UserResponse;

public interface UserService {
    UserResponse  getUserInfo();

    void deleteUser();

    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
}
