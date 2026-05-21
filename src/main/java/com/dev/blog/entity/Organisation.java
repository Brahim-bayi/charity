package com.dev.blog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organisations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String adresseLegale;
    private String numeroFiscal;
    private String contactPrincipal;
    private String logo;

    @Column(columnDefinition = "TEXT")
    private String descriptionMissions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Statut statut = Statut.EN_ATTENTE;

    public enum Statut { EN_ATTENTE, ACTIF, REJETE }

    @OneToOne
    @JoinColumn(name = "admin_id")
    private Utilisateur admin;
}
