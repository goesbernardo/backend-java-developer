package com.cmanager.app.integration.repository;

import com.cmanager.app.application.domain.Episode;
import com.cmanager.app.integration.dto.EpisodeAverageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode,String > {

    boolean existsByIdIntegration(Integer idIntegration);

    @Query("""
    SELECT new com.cmanager.app.integration.dto.EpisodeAverageDTO(e.season, AVG(e.rating))
    FROM Episode e
    GROUP BY e.season
""")
    List<EpisodeAverageDTO> findAverageRatingBySeason();
}
