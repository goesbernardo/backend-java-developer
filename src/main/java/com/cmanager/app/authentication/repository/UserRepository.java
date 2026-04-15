package com.cmanager.app.authentication.repository;

import com.cmanager.app.authentication.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Page<User> findByUsernameContainingIgnoreCase(String name, Pageable pageable);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
