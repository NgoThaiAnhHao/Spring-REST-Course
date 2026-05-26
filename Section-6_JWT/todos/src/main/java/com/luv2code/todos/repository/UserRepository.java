package com.luv2code.todos.repository;

import com.luv2code.todos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
        SELECT COUNT(U) FROM User U 
            JOIN U.authorities A
        WHERE A.authority = 'ROLE_ADMIN'
    """)
    long countAdminUsers();
}
