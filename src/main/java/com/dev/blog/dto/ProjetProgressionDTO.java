package com.dev.blog.dto;

import java.math.BigDecimal;

/**
 * Progression d'un projet vers son objectif de collecte.
 * Retourné par GET /api/projets/{id}/progression — utilisé pour afficher
 * la barre de progression sur la page publique du projet.
 * Le pourcentage peut dépasser 100 si l'objectif est dépassé.
 */
public record ProjetProgressionDTO(
        Long projetId,
        String nomProjet,
        BigDecimal objectifMontant,   // montant cible du projet
        BigDecimal montantCollecte,   // total des dons validés à ce jour
        double pourcentage            // (montantCollecte / objectifMontant) * 100
) {}
