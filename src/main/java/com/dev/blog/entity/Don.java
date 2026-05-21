package com.dev.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Don {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    private LocalDateTime dateDon;

    private String modePaiement;

    private String commentaire;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement")
    private StatutPaiement statutPaiement;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donateur_id", nullable = false)
    private Donateur donateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    public enum StatutPaiement {
        EN_ATTENTE, CONFIRME, ECHEC
    }

    @PrePersist
    protected void prePersist() {
        if (statutPaiement == null) {
            statutPaiement = StatutPaiement.CONFIRME;
        }
    }
}
