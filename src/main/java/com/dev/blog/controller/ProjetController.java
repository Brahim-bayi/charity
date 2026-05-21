package com.dev.blog.controller;

import com.dev.blog.dto.ProjetProgressionDTO;
import com.dev.blog.dto.ProjetStatistiquesDTO;
import com.dev.blog.entity.Distribution;
import com.dev.blog.entity.Don;
import com.dev.blog.entity.Projet;
import com.dev.blog.service.ProjetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Gère les projets caritatifs : création, suivi, statistiques et archivage.
 * Les projets peuvent être filtrés par statut ou catégorie, et exposent des métriques de collecte.
 */
@RestController
@RequestMapping("/api/projets")
public class ProjetController {

    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    @GetMapping
    public ResponseEntity<List<Projet>> findAll() {
        return ResponseEntity.ok(projetService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projet> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.findById(id));
    }

    // Filtre par statut : EN_COURS, TERMINE, ARCHIVE
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Projet>> findByStatut(@PathVariable Projet.StatutProjet statut) {
        return ResponseEntity.ok(projetService.findByStatut(statut));
    }

    // Recherche par nom (contient) — utile pour la barre de recherche du frontend
    @GetMapping("/recherche")
    public ResponseEntity<List<Projet>> rechercher(@RequestParam String nom) {
        return ResponseEntity.ok(projetService.rechercher(nom));
    }

    // Filtre par catégorie : EDUCATION, SANTE, ALIMENTATION, etc.
    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<Projet>> findByCategorie(@PathVariable Projet.Categorie categorie) {
        return ResponseEntity.ok(projetService.findByCategorie(categorie));
    }

    // Retourne les projets triés par montant collecté décroissant (page d'accueil)
    @GetMapping("/populaires")
    public ResponseEntity<List<Projet>> populaires() {
        return ResponseEntity.ok(projetService.findPopulaires());
    }

    /**
     * Archive un projet sans le supprimer : il reste visible mais n'accepte plus de dons.
     * Utilise PATCH car c'est une mise à jour partielle du statut uniquement.
     */
    @PatchMapping("/{id}/archiver")
    public ResponseEntity<Projet> archiver(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.archiver(id));
    }

    // Retourne tous les dons reçus pour ce projet
    @GetMapping("/{id}/dons")
    public ResponseEntity<List<Don>> findDons(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.findDonsByProjet(id));
    }

    // Retourne toutes les distributions effectuées depuis ce projet
    @GetMapping("/{id}/distributions")
    public ResponseEntity<List<Distribution>> findDistributions(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.findDistributionsByProjet(id));
    }

    // Somme totale des dons validés pour ce projet
    @GetMapping("/{id}/total-collecte")
    public ResponseEntity<BigDecimal> totalCollecte(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.totalDonsCollectes(id));
    }

    // Statistiques agrégées : nombre de donateurs, montant moyen, etc.
    @GetMapping("/{id}/statistiques")
    public ResponseEntity<ProjetStatistiquesDTO> statistiques(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.getStatistiques(id));
    }

    // Pourcentage d'avancement par rapport à l'objectif de collecte
    @GetMapping("/{id}/progression")
    public ResponseEntity<ProjetProgressionDTO> progression(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.getProgression(id));
    }

    @PostMapping
    public ResponseEntity<Projet> create(@RequestBody Projet projet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetService.save(projet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projet> update(@PathVariable Long id, @RequestBody Projet projet) {
        return ResponseEntity.ok(projetService.update(id, projet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}