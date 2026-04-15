package com.cmanager.app.integration.dto;

import com.cmanager.app.authentication.data.UserDTO;
import com.cmanager.app.authentication.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Schema(name = "ShowsDTO", description = "Objeto da representação de Shows")
@JsonIgnoreProperties(ignoreUnknown = true)
public record EpisodeRequestDTO(
        @JsonProperty("id")
        @Schema(name = "id", description = "Id")
        Integer id,
        @JsonProperty("name")
        @Schema(name = "name", description = "Nome")
        String name,
        @JsonProperty("season")
        @Schema(name = "season", description = "Temporada")
        Integer season,
        @JsonProperty("number")
        @Schema(name = "number", description = "Episódio")
        Integer number,
        @JsonProperty("type")
        @Schema(name = "type", description = "Tipo")
        String type,
        @JsonProperty("airdate")
        @Schema(name = "airdate", description = "Lançamento")
        String airdate,
        @JsonProperty("airtime")
        @Schema(name = "airtime", description = "Hora de lançamento")
        String airtime,
        @JsonProperty("airstamp")
        @Schema(name = "airstamp", description = "Airstamp")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime airstamp,
        @JsonProperty("runtime")
        @Schema(name = "runtime", description = "Duração")
        Integer runtime,
        @JsonProperty("rating")
        @Schema(name = "rating", description = "Nota")
        RatingDTO rating,
        @JsonProperty("summary")
        @Schema(name = "summary", description = "Resumo")
        String summary
) {
    public static UserDTO convertEntity(User u) {
        return new UserDTO(u.getId(), u.getUsername(), u.getRole(), u.isEnabled());
    }
}
