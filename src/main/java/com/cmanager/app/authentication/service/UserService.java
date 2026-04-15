package com.cmanager.app.authentication.service;

import com.cmanager.app.authentication.data.UserCreateRequest;
import com.cmanager.app.authentication.data.UserDTO;
import com.cmanager.app.authentication.data.UserUpdateRequest;
import com.cmanager.app.authentication.domain.User;
import com.cmanager.app.authentication.repository.UserRepository;
import com.cmanager.app.core.exception.AlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> list() {
        return userRepository.findAll().stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getRole(), u.isEnabled()))
                .toList();
    }

    @Transactional(readOnly = true)
    public User get(String id) {
        return this.findById(id);
    }

    @Transactional
    public User create(UserCreateRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new AlreadyExistsException("User", req.username());
        }
        final User u = new User();
        u.setUsername(req.username());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRole(req.role());
        u.setEnabled(req.enabled());
        return userRepository.save(u);
    }

    @Transactional
    public User update(String id, UserUpdateRequest req) {
        final User u = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (StringUtils.isNotBlank(req.username())) {
            userRepository.findByUsername(req.username())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(s -> {
                        throw new AlreadyExistsException("User", req.username());
                    });
            u.setUsername(req.username());
        }
        if (StringUtils.isNotBlank(req.password())) u.setPassword(passwordEncoder.encode(req.password()));
        if (req.role() != null) u.setRole(req.role());
        if (req.enabled() != null) u.setEnabled(req.enabled());
        return userRepository.save(u);
    }

    @Transactional
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Page<User> findByUsernameContainingIgnoreCase(String name, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(name, pageable);
    }
}
