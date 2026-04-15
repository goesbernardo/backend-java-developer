package com.cmanager.app.service;

import com.cmanager.app.authentication.data.UserCreateRequest;
import com.cmanager.app.authentication.data.UserUpdateRequest;
import com.cmanager.app.authentication.domain.Role;
import com.cmanager.app.authentication.domain.User;
import com.cmanager.app.authentication.repository.UserRepository;
import com.cmanager.app.authentication.service.UserService;
import com.cmanager.app.config.AbstractPostgresContainerIT;
import com.cmanager.app.core.exception.AlreadyExistsException;
import com.cmanager.app.core.utils.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@Rollback
class UserServiceIT extends AbstractPostgresContainerIT {

    @Autowired
    UserService service;

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordEncoder passwordEncoder;

    String existingId;

    @BeforeEach
    void seed() {
        repository.deleteAll();
        User u = new User();
        u.setUsername("seed");
        u.setPassword(passwordEncoder.encode("seedpass"));
        u.setRole(Role.ADMIN);
        u.setEnabled(true);
        existingId = repository.save(u).getId();
    }

    @Test
    @DisplayName("create() salva, encoda senha e enabled default true quando null")
    void create_ok() {
        var req = new UserCreateRequest("alice", "pwd", Role.USER, true);
        User saved = service.create(req);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.isEnabled()).isTrue();
        assertThat(passwordEncoder.matches("pwd", saved.getPassword())).isTrue();

        var req2 = new UserCreateRequest("alice", "pwd", Role.USER, true);
        User saved2 = service.create(req2);
        assertThat(saved2.getId()).isNotNull();
    }

    @Test
    @DisplayName("update() altera somente campos não nulos e re-encoda senha")
    void update_ok() {
        var req = new UserUpdateRequest("seed-upd", "newpass", Role.USER, false);
        User upd = service.update(existingId, req);

        assertThat(upd.getUsername()).isEqualTo("seed-upd");
        assertThat(upd.getRole()).isEqualTo(Role.USER);
        assertThat(upd.isEnabled()).isFalse();
        assertThat(passwordEncoder.matches("newpass", upd.getPassword())).isTrue();
    }

    @Test
    @DisplayName("findById retorna entidade existente e lança quando não existe")
    void findById_ok_and_fail() {
        var found = service.findById(existingId);
        assertThat(found.getUsername()).isEqualTo("seed");

        assertThatThrownBy(() -> service.findById("does-not-exist"))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }

    @Test
    @DisplayName("findByUsernameContainingIgnoreCase usa paginação/ordenação do Util")
    void findByUsername_paged() {
        // mais dados
        service.create(new UserCreateRequest("alex", "x", Role.USER, true));
        service.create(new UserCreateRequest("alberto", "x", Role.USER, true));
        service.create(new UserCreateRequest("bob", "x", Role.USER, true));

        var pageable = Util.getPageable(0, 2, "username", "ASC");
        var page = service.findByUsernameContainingIgnoreCase("al", pageable);

        assertThat(page.getTotalElements()).isEqualTo(2); // alex, alberto
        assertThat(page.getContent()).extracting(User::getUsername)
                .containsExactly("alberto", "alex");
    }

    @Test
    @DisplayName("delete() remove o usuário")
    void delete_ok() {
        service.delete(existingId);
        assertThat(repository.findById(existingId)).isEmpty();
    }

    @Test
    @DisplayName("create() deve lançar UsernameAlreadyExistsException quando username já existe")
    void create_conflict_username() {
        // seed já criou "seed"
        var dup = new UserCreateRequest("seed", "pwd", Role.USER, true);
        assertThatThrownBy(() -> service.create(dup))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    @DisplayName("update() deve lançar UsernameAlreadyExistsException quando mudar para username já usado por outro")
    void update_conflict_username() {
        // cria outro usuário "bob"
         service.create(new UserCreateRequest("bob", "x", Role.USER, true));
        // tenta renomear seed -> bob
        var req = new UserUpdateRequest("bob", null, null, null);
        assertThatThrownBy(() -> service.update(existingId, req))
                .isInstanceOf(AlreadyExistsException.class);
    }
}
