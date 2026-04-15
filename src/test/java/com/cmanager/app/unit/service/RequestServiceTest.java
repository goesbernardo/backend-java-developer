package com.cmanager.app.unit.service;

import com.cmanager.app.application.domain.Episode;
import com.cmanager.app.integration.client.AbstractRequest;
import com.cmanager.app.integration.client.RequestService;
import com.cmanager.app.integration.client.ShowPersistenceService;
import com.cmanager.app.integration.dto.EpisodeAverageDTO;
import com.cmanager.app.integration.dto.ShowsRequestDTO;
import com.cmanager.app.integration.repository.EpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private EpisodeRepository episodeRepository;

    @Mock
    private AbstractRequest<ShowsRequestDTO> abstractConnect;

    @Mock
    private ShowPersistenceService showPersistenceService;

    @InjectMocks
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getAverageBySeason - deve calcular média corretamente")
    void getAverageBySeason_ShouldCalculateCorrectly() {
        Episode e1 = new Episode();
        e1.setSeason(1);
        e1.setRating(new BigDecimal("8.0"));

        Episode e2 = new Episode();
        e2.setSeason(1);
        e2.setRating(new BigDecimal("9.0"));

        when(episodeRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EpisodeAverageDTO> result = requestService.getAverageBySeason();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).season()).isEqualTo(1);
        assertThat(result.get(0).average()).isEqualByComparingTo("8.50");
    }

    @Test
    @DisplayName("getAverageBySeason - deve lançar exceção quando não há episódios")
    void getAverageBySeason_ShouldThrowException_WhenEmpty() {
        when(episodeRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> requestService.getAverageBySeason())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("não existem episódios cadastrados");
    }

    @Test
    @DisplayName("getShow - deve lançar AccessDeniedException para usuário sem ROLE_ADMIN")
    void getShow_ShouldThrowException_WhenNotAdmin() {
        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(auth).getAuthorities();
        
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        assertThatThrownBy(() -> requestService.getShow("Girls"))
                .isInstanceOf(AccessDeniedException.class);
    }
}
