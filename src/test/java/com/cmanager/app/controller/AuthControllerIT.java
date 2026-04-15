package com.cmanager.app.controller;

import com.cmanager.app.authentication.data.LoginRequest;
import com.cmanager.app.config.AbstractPostgresContainerIT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerIT extends AbstractPostgresContainerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtDecoder jwtDecoder;

    @TestConfiguration
    static class TestUsersConfig {
        /**
         * Define um UserDetailsService em memória para os testes.
         * O AuthenticationManager (de SecurityConfig) vai usar esse bean.
         */
        @Bean
        UserDetailsService userDetailsService(PasswordEncoder encoder) {
            var admin = User.withUsername("alice")
                    .password(encoder.encode("password"))
                    .roles("ADMIN") // gera ROLE_ADMIN
                    .build();
            var user = User.withUsername("bob")
                    .password(encoder.encode("password"))
                    .roles("USER") // gera ROLE_USER
                    .build();
            return new InMemoryUserDetailsManager(admin, user);
        }
    }

    @Test
    @DisplayName("POST /api/auth/login - deve retornar 200 e JWT com claims esperadas")
    void login_ok_returnsJwtWithExpectedClaims() throws Exception {
        var payload = new LoginRequest("alice", "password");
        var body = objectMapper.writeValueAsString(payload);

        var mvcResult = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        // Decodifica o token para validar claims
        var json = mvcResult.getResponse().getContentAsString();
        var tokenNode = objectMapper.readTree(json).get("token");
        assertThat(tokenNode).isNotNull();
        var token = tokenNode.asText();

        Jwt jwt = jwtDecoder.decode(token);

        assertThat(jwt.getClaimAsString(JwtClaimNames.ISS)).isEqualTo("cmanage-tests");
        assertThat(jwt.getSubject()).isEqualTo("alice");
        assertThat(jwt.getClaimAsString("role")).isEqualTo("ROLE_ADMIN");

        // exp deve ser ~ agora + 30min (tolerância)
        Instant exp = jwt.getExpiresAt();
        assertThat(exp).isNotNull();
        var now = Instant.now();
        assertThat(exp).isAfter(now.plus(25, ChronoUnit.MINUTES));
        assertThat(exp).isBefore(now.plus(40, ChronoUnit.MINUTES));
    }

    @Test
    @DisplayName("POST /api/auth/login - com senha inválida deve retornar 401")
    void login_fail_wrongPassword_returns401() throws Exception {
        var payload = new LoginRequest("alice", "wrong");
        var body = objectMapper.writeValueAsString(payload);

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        ).andExpect(status().isUnauthorized());
    }
}
