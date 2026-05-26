package com.luv2code.todos.service;

import com.luv2code.todos.entity.Authority;
import com.luv2code.todos.entity.Todo;
import com.luv2code.todos.entity.User;
import com.luv2code.todos.repository.UserRepository;
import com.luv2code.todos.response.TodoResponse;
import com.luv2code.todos.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===================================== GET ALL USERS =====================================
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getAuthorities().stream().map(authority -> (Authority) authority).toList()
        );
    }

    // ===================================== PROMOTE USER TO ADMIN =====================================
    @Override
    @Transactional
    public UserResponse promoteToAdmin(long userId) {
        // Find user
        Optional<User> user = userRepository.findById(userId);

        // Check user not found
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND");
        }

        // Check user already admin before
        if (user.get().getAuthorities().stream().anyMatch(authority ->
                "ROLE_ADMIN".equals(authority.getAuthority()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER ALREADY BEFORE");
        }

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_EMPLOYEE"));
        authorities.add(new Authority("ROLE_ADMIN"));
        user.get().setAuthorities(authorities);

        // Save to database
        User savedUser = userRepository.save(user.get());
        return convertToUserResponse(savedUser);
    }

    // ===================================== DELETE NON ADMIN USER =====================================
    @Override
    @Transactional
    public void deleteNonAdminUser(long userId) {
        // Find user
        Optional<User> user = userRepository.findById(userId);

        // Check user not found
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND");
        }

        // Check user already admin before
        if (user.get().getAuthorities().stream().anyMatch(authority ->
                "ROLE_ADMIN".equals(authority.getAuthority()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER IS ADMIN, CAN NOT DELETE");
        }

        // Delete from database
        userRepository.delete(user.get());
    }


}
