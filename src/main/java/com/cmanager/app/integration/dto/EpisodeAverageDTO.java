package com.cmanager.app.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EpisodeAverageDTO", description = "Média de rating por temporada")
public record EpisodeAverageDTO(
        @Schema(description = "Número da temporada", example = "1")
        Integer season,
        @Schema(description = "Média de rating da temporada", example = "8.5")
        Double average
) {
}
