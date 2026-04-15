package com.cmanager.app.repository;

import com.cmanager.app.authentication.domain.Role;
import com.cmanager.app.authentication.domain.User;
import com.cmanager.app.authentication.repository.UserRepository;
import com.cmanager.app.config.AbstractPostgresContainerIT;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// 👇 garante que o slice enxergue sua entidade e repo mesmo se estiverem em módulos/pacotes diferentes
@EntityScan(basePackageClasses = User.class)
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
class UserRepositoryIT extends AbstractPostgresContainerIT {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.save(newUser("alice", Role.USER));
        userRepository.save(newUser("bob", Role.USER));
        userRepository.save(newUser("alex", Role.ADMIN));
        userRepository.save(newUser("charlie", Role.ADMIN));
        userRepository.save(newUser("alberto", Role.USER));
    }

    private User newUser(String username, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("{noop}pwd");
        u.setRole(role);
        u.setEnabled(true);
        return u;
    }

    @Test
    @DisplayName("findByUsernameContainingIgnoreCase - filtra, pagina e ordena por username ASC")
    void findByUsernameContainingIgnoreCase_paged_sorted() {
        var pageable = PageRequest.of(0, 2, Sort.by("username").ascending());
        Page<User> page = userRepository.findByUsernameContainingIgnoreCase("al", pageable);

        assertThat(page.getTotalElements()).isEqualTo(3); // alice, alex, alberto
        assertThat(page.getContent()).extracting(User::getUsername)
                .containsExactly("alberto", "alex"); // 1ª página (ASC)
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(2);

        var next = userRepository.findByUsernameContainingIgnoreCase("al", pageable.next());
        assertThat(next.getContent()).extracting(User::getUsername)
                .containsExactly("alice"); // 2ª página
    }

    @Test
    @DisplayName("existsByUsername funciona")
    void existsByUsername_ok() {
        assertThat(userRepository.existsByUsername("charlie")).isTrue();
        assertThat(userRepository.existsByUsername("nobody")).isFalse();
    }

    @Test
    @DisplayName("username deve ser único (constraint)")
    void username_unique_constraint() {
        final var userException =  newUser("alex", Role.USER);
        Assert.assertThrows(DataIntegrityViolationException.class, () ->userRepository.saveAndFlush(userException));
    }
}
