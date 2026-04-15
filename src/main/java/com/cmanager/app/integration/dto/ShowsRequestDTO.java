package com.cmanager.app.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ShowsDTO", description = "Objeto da representação de Shows")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShowsRequestDTO(
        @JsonProperty("id")
        @Schema(name = "id", description = "Id")
        Integer id,
        @JsonProperty("name")
        @Schema(name = "name", description = "Nome")
        String name,
        @JsonProperty("type")
        @Schema(name = "type", description = "Tipo")
        String type,
        @JsonProperty("language")
        @Schema(name = "language", description = "Lingua")
        String language,
        @JsonProperty("status")
        @Schema(name = "status", description = "Status")
        String status,
        @JsonProperty("runtime")
        @Schema(name = "runtime", description = "Duração")
        Integer runtime,
        @JsonProperty("averageRuntime")
        @Schema(name = "averageRuntime", description = "Tempo médio")
        Integer averageRuntime,
        @JsonProperty("officialSite")
        @Schema(name = "officialSite", description = "Site oficial")
        String officialSite,
        @JsonProperty("rating")
        @Schema(name = "rating", description = "Nota")
        RatingDTO rating,
        @JsonProperty("summary")
        @Schema(name = "summary", description = "Resumo")
        String summary,
        @JsonProperty("_embedded")
        @Schema(name = "_embedded", description = "Resumo")
        Embedded _embedded

) {
    public record Embedded(
            List<EpisodeRequestDTO> episodes
    ) {
    }
}
