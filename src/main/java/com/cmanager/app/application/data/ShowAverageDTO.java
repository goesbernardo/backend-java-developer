package com.cmanager.app.application.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "ShowDTO", description = "Response da sincronização de TV shows")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShowAverageDTO(

        @JsonProperty("id")
        @Schema(name = "id", description = "Id do tv show")
        String id,
        @JsonProperty("name")
        @Schema(name = "name", description = "Nome do tv show")
        String name,
        @JsonProperty("rating")
        @Schema(name = "rating", description = "Nota média")
        BigDecimal rating

) {
}

