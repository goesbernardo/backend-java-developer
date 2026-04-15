package com.cmanager.app.authentication.data;

import com.cmanager.app.authentication.domain.Role;
import com.cmanager.app.authentication.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserDTO", description = "Response da entidade de usuario")
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDTO(
        @JsonProperty("id")
        @Schema(name = "id", description = "Id do usuário")
        String id,
        @JsonProperty("username")
        @Schema(name = "username", description = "Username do usuário")
        String username,
        @JsonProperty("role")
        @Schema(name = "role", description = "Role do usuário")
        Role role,
        @JsonProperty("enabled")
        @Schema(name = "enabled", description = "Usuário ativo")
        boolean enabled
) {
    public static UserDTO convertEntity(User u) {
        return new UserDTO(u.getId(), u.getUsername(), u.getRole(), u.isEnabled());
    }
}
