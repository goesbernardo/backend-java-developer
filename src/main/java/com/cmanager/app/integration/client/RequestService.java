package com.cmanager.app.integration.client;

import com.cmanager.app.integration.dto.EpisodeAverageDTO;
import com.cmanager.app.integration.dto.ShowsRequestDTO;
import com.cmanager.app.integration.repository.EpisodeRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (episodeRepository.count() == 0) {
            throw new RuntimeException("não existem episódios cadastrados");
        }
        return episodeRepository.findAverageRatingBySeason().stream()
                .map(dto -> new EpisodeAverageDTO(dto.season(), dto.average() != null ? dto.average() : 0.0))
                .toList();
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
