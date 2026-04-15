package com.cmanager.app.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "ShowsDTO", description = "Objeto da representação de Rating")
@JsonIgnoreProperties(ignoreUnknown = true)
public record RatingDTO(
        @JsonProperty("average")
        @Schema(name = "average", description = "Média")
        BigDecimal average
) {
}
