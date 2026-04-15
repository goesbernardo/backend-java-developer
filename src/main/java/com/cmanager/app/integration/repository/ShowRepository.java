package com.cmanager.app.integration.repository;

import com.cmanager.app.application.domain.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show,String> {

    Optional<Show> findByIdIntegration(Integer idIntegration);
}
