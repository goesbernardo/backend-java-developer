package com.cmanager.app.integration.client;

import com.cmanager.app.application.domain.Episode;
import com.cmanager.app.application.domain.Show;
import com.cmanager.app.integration.dto.EpisodeRequestDTO;
import com.cmanager.app.integration.dto.ShowsRequestDTO;
import com.cmanager.app.integration.repository.EpisodeRepository;
import com.cmanager.app.integration.repository.ShowRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ShowPersistenceService {

    private final ShowRepository showRepository;
    private final EpisodeRepository episodeRepository;

    public ShowPersistenceService(ShowRepository showRepository,
                                  EpisodeRepository episodeRepository) {
        this.showRepository = showRepository;
        this.episodeRepository = episodeRepository;
    }

    @Transactional
    public void saveShowAndEpisodes(ShowsRequestDTO dto) {

        Optional<Show> existingShow = showRepository.findByIdIntegration(dto.id());

        Show show;

        if (existingShow.isPresent()) {
            show = existingShow.get();
        } else {
            show = new Show();
            show.setIdIntegration(dto.id());
            show.setName(dto.name());
            show.setType(dto.type());
            show.setLanguage(dto.language());
            show.setStatus(dto.status());
            show.setRuntime(dto.runtime());
            show.setAverageRuntime(dto.averageRuntime());
            show.setOfficialSite(dto.officialSite());
            show.setRating(dto.rating() != null ? dto.rating().average() : null);
            show.setSummary(dto.summary());
        }

        show.getEpisodes().clear();

        if (dto._embedded() != null && dto._embedded().episodes() != null) {

            for (EpisodeRequestDTO epDto : dto._embedded().episodes()) {

                boolean exists = episodeRepository.existsByIdIntegration(epDto.id());

                if (!exists) {

                    Episode episode = new Episode();
                    episode.setIdIntegration(epDto.id());
                    episode.setName(epDto.name());
                    episode.setSeason(epDto.season());
                    episode.setNumber(epDto.number());
                    episode.setType(epDto.type());
                    episode.setRuntime(epDto.runtime());
                    episode.setSummary(epDto.summary());


                    if (epDto.airstamp() != null) {
                        episode.setAirstamp(epDto.airstamp().toLocalDateTime());
                    }

                    episode.setShow(show);
                    show.getEpisodes().add(episode);
                }
            }
        }

        showRepository.saveAndFlush(show);
    }
    }

