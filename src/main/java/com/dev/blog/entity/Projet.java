package com.dev.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "projets")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String lieu;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal objectifMontant;

    @Column(precision = 12, scale = 2)
    private BigDecimal montantCollecte = BigDecimal.ZERO;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProjet statut;

    public enum StatutProjet {
        EN_COURS, TERMINE, ANNULE, EN_ATTENTE, ARCHIVE
    }

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    public enum Categorie {
        EDUCATION, SANTE, ENVIRONNEMENT, HUMANITAIRE, CULTURE, AUTRE
    }

    private String imageUrl;
    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Don> dons;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Distribution> distributions;
}