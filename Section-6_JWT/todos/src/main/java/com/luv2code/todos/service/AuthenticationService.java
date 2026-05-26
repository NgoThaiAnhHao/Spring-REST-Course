package com.luv2code.todos.service;

import com.luv2code.todos.request.AuthenticationRequest;
import com.luv2code.todos.request.RegisterRequest;
import com.luv2code.todos.response.AuthenticationResponse;

public interface AuthenticationService {
    void register(RegisterRequest input) throws Exception;

    AuthenticationResponse login(AuthenticationRequest request);
}
