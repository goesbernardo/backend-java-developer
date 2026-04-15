package com.cmanager.app.integration.client;

import com.cmanager.app.application.domain.Episode;
import com.cmanager.app.integration.dto.EpisodeAverageDTO;
import com.cmanager.app.integration.dto.ShowsRequestDTO;
import com.cmanager.app.integration.repository.EpisodeRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private static final String URL = "https://api.tvmaze.com/singlesearch/shows";

    private final EpisodeRepository episodeRepository;

    private final ShowPersistenceService showPersistenceService;

    private final AbstractRequest<ShowsRequestDTO> abstractConnect;

    public RequestService(EpisodeRepository episodeRepository, AbstractRequest<ShowsRequestDTO> abstractConnect
    ,ShowPersistenceService showPersistenceService) {
        this.episodeRepository = episodeRepository;
        this.abstractConnect = abstractConnect;
        this.showPersistenceService = showPersistenceService;
    }

    public List<EpisodeAverageDTO> getAverageBySeason(){
        List<Episode> episodes = episodeRepository.findAll();

        if (episodes.isEmpty()){
            throw new RuntimeException("não existem episódios cadastrados");
        }

        Map<Integer,List<Episode>> grouped = episodes.stream().collect(Collectors.groupingBy(Episode::getSeason));

        List<EpisodeAverageDTO> result = new ArrayList<>();

        for (Map.Entry<Integer,List<Episode>> entry : grouped.entrySet()) {
            Integer season = entry.getKey();

            List<BigDecimal> ratings = entry.getValue().stream().map(Episode::getRating).filter(Objects::nonNull).toList();

            BigDecimal average;

            if (ratings.isEmpty()){
                average = BigDecimal.ZERO;
            }else {
                average = ratings.stream().reduce(BigDecimal.ZERO,BigDecimal::add).divide(BigDecimal.valueOf(ratings.size()),2, RoundingMode.HALF_UP);
            }
            result.add(new EpisodeAverageDTO(season,average));
        }
        return result;


    }


    public ShowsRequestDTO getShow(String showName) {
        validateAdmin();
        String url = URL + "?q=" + showName + "&embed=episodes";
        ShowsRequestDTO showsRequestDTO = abstractConnect.getShow(url,new ParameterizedTypeReference<>() {});
        showPersistenceService.saveShowAndEpisodes(showsRequestDTO);

        return showsRequestDTO;
    }

    private void validateAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("Apenas ADMIN pode salvar");
        }
    }

}
