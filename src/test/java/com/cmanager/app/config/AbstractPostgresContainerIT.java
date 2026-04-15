package com.cmanager.app.config;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class AbstractPostgresContainerIT {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    @BeforeAll
    static void startContainer() {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerDatasource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("app.jwt.secret", () -> "test-secret-CHANGE-ME-32chars-min");
        registry.add("app.jwt.issuer", () -> "cmanage-tests");
        registry.add("app.jwt.expiration-minutes", () -> "30");

        // Caso use Flyway/Liquibase e não queira rodar aqui:
        // registry.add("spring.flyway.enabled", () -> "false");
        // registry.add("spring.liquibase.enabled", () -> "false");
    }
}
