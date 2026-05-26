package com.luv2code.todos.service;

import com.luv2code.todos.entity.Authority;
import com.luv2code.todos.entity.User;
import com.luv2code.todos.repository.UserRepository;
import com.luv2code.todos.request.AuthenticationRequest;
import com.luv2code.todos.request.RegisterRequest;
import com.luv2code.todos.response.AuthenticationResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // ===================================== REGISTER =====================================

    @Override
    @Transactional
    public void register(RegisterRequest input) throws Exception {

        if (isEmailExist(input.getEmail())) {
            throw new Exception("Email already exist");
        }

        User user = buildNewUser(input);
        userRepository.save(user);
    }

    // Checking duplicate email
    private boolean isEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Mapper RegisterRequest -> User Entity
    private User buildNewUser(RegisterRequest input) {
        User user = new User();

        user.setId(0);
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setPassword(
                passwordEncoder.encode(input.getPassword())
        );
        user.setAuthorities(initialAuthority());

        return user;
    }

    private List<Authority> initialAuthority() {
        boolean isFirstUser = userRepository.count() == 0;
        List<Authority> authorities = new ArrayList<>();

        authorities.add(
                new Authority("ROLE_EMPLOYEE")
        );

        if (isFirstUser) {
            authorities.add(
                    new Authority("ROLE_ADMIN")
            );
        }

        return authorities;
    }

    // ===================================== LOGIN =====================================
    @Override
    @Transactional(readOnly = true) // Just read data
    public AuthenticationResponse login(AuthenticationRequest request) {

        // Authenticate
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword())
        );

        // Checking email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new IllegalArgumentException("Invalid email")
                );

        // Create JWT Token
        String jwtToken = jwtService.generateToken(new HashMap<>(), user);

        return new AuthenticationResponse(jwtToken);
    }

}













