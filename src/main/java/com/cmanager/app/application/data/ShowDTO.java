package com.cmanager.app.application.data;

import com.cmanager.app.application.domain.Show;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ShowDTO", description = "Response da sincronização de TV shows")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShowDTO(

        @JsonProperty("id")
        @Schema(name = "id", description = "Id do tv show")
        String id,
        @JsonProperty("name")
        @Schema(name = "name", description = "Nome do tv show")
        String name

) {
    public static ShowDTO convertEntity(Show entity) {
        return new ShowDTO(entity.getId(), entity.getName());
    }
}

