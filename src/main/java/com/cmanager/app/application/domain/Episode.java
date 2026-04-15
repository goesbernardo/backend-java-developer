package com.cmanager.app.application.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "EPISODE")
@Data
public class Episode {

    @Id
    private String id;

    @Column(name = "id_integration", unique = true)
    private Integer idIntegration;

    private String name;

    private Integer season;

    private Integer number;

    private String type;

    private Integer runtime;

    @Column(columnDefinition = "CLOB")
    private String summary;

    private LocalDateTime airstamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    private Show show;

    private BigDecimal rating;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID().toString();
    }
}
