package com.dev.blog.dto;

import java.math.BigDecimal;

/**
 * Agrégat de statistiques d'un projet retourné par GET /api/projets/{id}/statistiques.
 * Combine en une seule réponse les données de dons et de distributions pour éviter
 * plusieurs appels API depuis le frontend.
 */
public record ProjetStatistiquesDTO(
        Long projetId,
        String nomProjet,
        BigDecimal objectifMontant,      // montant cible fixé à la création du projet
        BigDecimal totalDons,            // somme de tous les dons validés
        long nombreDons,                 // nombre total de transactions de don
        long nombreDistributions,        // nombre de distributions effectuées vers des bénéficiaires
        BigDecimal totalDistributions    // montant total distribué aux bénéficiaires
) {}
