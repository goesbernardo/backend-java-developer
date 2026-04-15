package com.cmanager.app.application.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "SHOW")
@Data
public class Show {

    @Id
    private String id;
    @Column(name = "ID_INTEGRATION")
    private Integer idIntegration;
    @Column(name = "NAME")
    private String name;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "LANGUAGE")
    private String language;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "RUNTIME")
    private Integer runtime;
    @Column(name = "AVERAGE_RUNTIME")
    private Integer averageRuntime;
    @Column(name = "OFFICIAL_SITE")
    private String officialSite;
    @Column(name = "RATING", precision = 5, scale = 2)
    private BigDecimal rating;
    @Column(name = "SUMMARY", columnDefinition = "TEXT")
    @Lob
    private String summary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        final LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.id = UUID.randomUUID().toString();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
