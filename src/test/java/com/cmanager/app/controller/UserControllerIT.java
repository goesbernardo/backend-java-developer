package com.cmanager.app.controller;


import com.cmanager.app.authentication.domain.Role;
import com.cmanager.app.authentication.domain.User;
import com.cmanager.app.authentication.repository.UserRepository;
import com.cmanager.app.config.AbstractPostgresContainerIT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerIT extends AbstractPostgresContainerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    String existingId;
    String seededUsername;

    @BeforeEach
    void seed() {
        userRepository.deleteAll();
        User u = new User();
        seededUsername = "seed_" + java.util.UUID.randomUUID();
        u.setUsername(seededUsername);
        u.setPassword(passwordEncoder.encode("seedpass"));
        u.setRole(Role.ADMIN);
        u.setEnabled(true);
        existingId = userRepository.save(u).getId();

        // dados extra para paginação
        save("alberto");
        save("alex");
        save("bob");
    }

    private void save(String username) {
        var u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode("x"));
        u.setRole(Role.USER);
        u.setEnabled(true);
        userRepository.save(u);
    }

    @Test
    @DisplayName("GET /api/users - ADMIN: 200 com PageResultResponse e ordenação ASC por username")
    @WithMockUser(roles = "ADMIN")
    void list_admin_ok() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("username", "al")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortField", "username")
                        .param("sortOrder", "ASC"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // estrutura do PageResultResponse
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").value(2)) // alberto, alex
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                // itens ordenados
                .andExpect(jsonPath("$.items[0].username").value("alberto"))
                .andExpect(jsonPath("$.items[1].username").value("alex"));
    }


    @Test
    @DisplayName("GET /api/users - USER: 403")
    @WithMockUser(roles = "USER")
    void list_user_forbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/users/{id} - USER/ADMIN permitido, retorna 200 e DTO")
    @WithMockUser(roles = "USER")
    void get_ok() throws Exception {
        mockMvc.perform(get("/api/users/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.username").value(seededUsername))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    @DisplayName("GET /api/users/{id} (USER) deve retornar 404 quando não existe")
    @WithMockUser(roles = "USER")
    void get_as_user_notfound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 9999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/users (ADMIN) deve criar e retornar 201 + Location")
    @WithMockUser(roles = "ADMIN")
    void create_as_admin_created() throws Exception {
        var payload = """
                {
                  "username": "alice",
                  "password": "pwd123456",
                  "role": "USER",
                  "enabled": true
                }
                """;
        var res = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/users/")))
                .andExpect(jsonPath("$.username").value("alice"))
                .andReturn();

        // sanity check persisted:
        var body = res.getResponse().getContentAsString();
        var id = om.readTree(body).get("id").asText();
        assertThat(userRepository.findById(id)).isPresent();
    }

    @Test
    @DisplayName("POST /api/users - ADMIN: 409 quando username já existe")
    @WithMockUser(roles = "ADMIN")
    void create_admin_conflict409() throws Exception {
        // seededUsername já existe
        var payload = """
                { "username":"%s","password":"pwd123456","role":"USER","enabled":true }
                """.formatted(seededUsername);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} (ADMIN) deve atualizar e retornar 200")
    @WithMockUser(roles = "ADMIN")
    void update_as_admin_ok() throws Exception {
        var payload = """
                {
                  "username": "seed-updated",
                  "password": "newpass",
                  "role": "USER",
                  "enabled": false
                }
                """;
        mockMvc.perform(put("/api/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("seed-updated"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - ADMIN: 409 quando mudar para username existente")
    @WithMockUser(roles = "ADMIN")
    void update_admin_conflict409() throws Exception {

        var payload = """
                {
                  "username": "bob",
                  "password": "newpass",
                  "role": "USER",
                  "enabled": false
                }
                """;
        mockMvc.perform(put("/api/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} (ADMIN) deve retornar 204 e remover")
    @WithMockUser(roles = "ADMIN")
    void delete_as_admin_noContent() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", existingId))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(existingId)).isEmpty();
    }
}
