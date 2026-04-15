package com.cmanager.app.integration.controller;

import com.cmanager.app.integration.client.RequestService;
import com.cmanager.app.integration.dto.EpisodeAverageDTO;
import com.cmanager.app.integration.dto.ShowsRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("api/v1/shows")
@RestController
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }



    @Operation(
            summary = "Buscar show por nome",
            description = "Consulta a API externa TVMaze pelo nome do show, persiste no banco (show + episódios) " +
                    "e retorna os dados. Apenas usuários ADMIN podem executar."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Show encontrado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (não é ADMIN)"),
            @ApiResponse(responseCode = "500", description = "Erro ao consumir API externa")
    })
    @GetMapping
    public ResponseEntity<ShowsRequestDTO> findShows(@RequestParam String showName) {
            ShowsRequestDTO showsRequestDTO = requestService.getShow(showName);
            return  ResponseEntity.ok(showsRequestDTO);
        }



    @Operation(
            summary = "Média de rating por temporada",
            description = "Agrupa episódios por temporada e calcula a média dos ratings. " +
                    "Ignora valores nulos. Caso todos sejam nulos, retorna 0. " +
                    "Se não houver episódios cadastrados, retorna erro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Média calculada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não existem episódios cadastrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/episodes/average")
    public ResponseEntity<List<EpisodeAverageDTO>> getAverage() {
        return ResponseEntity.ok(requestService.getAverageBySeason());
    }
    }



