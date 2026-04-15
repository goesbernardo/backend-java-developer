package com.cmanager.app.unit.service;

import com.cmanager.app.authentication.data.UserCreateRequest;
import com.cmanager.app.authentication.data.UserUpdateRequest;
import com.cmanager.app.authentication.domain.Role;
import com.cmanager.app.authentication.domain.User;
import com.cmanager.app.authentication.repository.UserRepository;
import com.cmanager.app.authentication.service.UserService;
import com.cmanager.app.core.exception.AlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEnabled(true);
    }

    @Test
    @DisplayName("create - deve salvar novo usuário quando username não existe")
    void create_ShouldSaveUser_WhenUsernameDoesNotExist() {
        UserCreateRequest request = new UserCreateRequest("newuser", "password", Role.USER, true);
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(request);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("create - deve lançar AlreadyExistsException quando username já existe")
    void create_ShouldThrowException_WhenUsernameExists() {
        UserCreateRequest request = new UserCreateRequest("testuser", "password", Role.USER, true);
        
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(AlreadyExistsException.class);
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("findById - deve retornar usuário quando existe")
    void findById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        User result = userService.findById("1");

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("findById - deve lançar EntityNotFoundException quando não existe")
    void findById_ShouldThrowException_WhenDoesNotExist() {
        when(userRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById("invalid"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("update - deve atualizar campos permitidos")
    void update_ShouldUpdateFields() {
        UserUpdateRequest request = new UserUpdateRequest("newusername", "newpassword", Role.ADMIN, false);
        
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("newusername")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.update("1", request);

        assertThat(result.getUsername()).isEqualTo("newusername");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.isEnabled()).isFalse();
        verify(passwordEncoder).encode("newpassword");
    }

    @Test
    @DisplayName("delete - deve chamar o repositório")
    void delete_ShouldCallRepository() {
        userService.delete("1");
        verify(userRepository).deleteById("1");
    }
}
