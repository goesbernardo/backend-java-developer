package com.cmanager.app.integration.repository;

import com.cmanager.app.application.domain.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode,String > {

    boolean existsByIdIntegration(Integer idIntegration);

    @Query("""
    SELECT e.season, AVG(e.rating)
    FROM Episode e
    WHERE e.rating IS NOT NULL
    GROUP BY e.season
""")
    List<Object[]> findAverageRatingBySeason();
}
