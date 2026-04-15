package com.cmanager.app.authentication.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Payload para login de um usuário")
@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginRequest(
        @NotBlank
        @Schema(name = "username", description = "Login para acesso")
        @JsonProperty("username")
        String username,
        @NotBlank
        @Schema(name = "password", description = "Senha do usuário")
        @JsonProperty("password")
        String password
) {
}
