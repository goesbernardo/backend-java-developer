package com.cmanager.app.authentication.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TokenResponse", description = "Payload de response com token para acesso")
@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenResponse(
        @JsonProperty("token")
        @Schema(name = "token", description = "Token do usuario")
        String token
) {
}
