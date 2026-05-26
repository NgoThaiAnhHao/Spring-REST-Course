package com.luv2code.todos.service;

import com.luv2code.todos.entity.Authority;
import com.luv2code.todos.entity.User;
import com.luv2code.todos.repository.UserRepository;
import com.luv2code.todos.request.PasswordUpdateRequest;
import com.luv2code.todos.response.UserResponse;
import com.luv2code.todos.util.FindAuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, FindAuthenticatedUser findAuthenticatedUser, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================ GET USER INFO ============================
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserInfo() {
        // Get current user who authenticated
        User user = findAuthenticatedUser.getAuthenticatedUser();

        return new UserResponse(
          user.getId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getAuthorities().stream().map(auth -> (Authority) auth).toList()
        );
    }

    // ============================ DELETE USER ============================
    @Override
    public void deleteUser() {
        // Get current user who authenticated
        User user = findAuthenticatedUser.getAuthenticatedUser();

        // Check is last admin
        if (isLastAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin cannot delete itself");
        }

        userRepository.delete(user);
    }

    private boolean isLastAdmin(User user) {
        boolean isAdmin = user.getAuthorities()
                .stream()
                .anyMatch(
                        authority -> "ROLE_ADMIN".equals(authority.getAuthority())
                );


        if (isAdmin) {
            long adminCount = userRepository.countAdminUsers();

            // True: Is last admin
            // False: Not the last admin
            return adminCount <= 1;
        }

        // User is not the admin
        return false;
    }

    // ============================ UPDATE PASSWORD ============================
    @Override
    @Transactional
    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
        // Get current user who authenticated
        User user = findAuthenticatedUser.getAuthenticatedUser();

        // Check old password and new password are the same?
        if (!isOldPasswordCorrect(user.getPassword(), passwordUpdateRequest.getOldPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        // Check new password are matches with new password confirmation
        if (!isNewPasswordConfirmed(passwordUpdateRequest.getNewPassword(), passwordUpdateRequest.getNewPasswordConfirmation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        // Check new password is different with old password
        if (!isNewPasswordDifferentOldPassword(passwordUpdateRequest.getOldPassword(), passwordUpdateRequest.getNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password and new passwords must be different");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);
    }

    private boolean isOldPasswordCorrect(String currentPassword, String oldPassword) {
        return passwordEncoder.matches(oldPassword, currentPassword);
    }

    private boolean isNewPasswordConfirmed(String newPassword, String newPasswordConfirmation) {
        return newPassword.equals(newPasswordConfirmation);
    }

    private boolean isNewPasswordDifferentOldPassword(String oldPassword, String newPassword) {
        return !oldPassword.equals(newPassword);
    }
}
