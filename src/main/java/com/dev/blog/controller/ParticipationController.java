package com.dev.blog.controller;

import com.dev.blog.entity.Participation;
import com.dev.blog.service.ParticipationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gère les participations des utilisateurs aux projets (bénévolat, inscription à un événement).
 * Une participation lie un utilisateur à un projet sans impliquer de don financier.
 */
@RestController
@RequestMapping("/api/participations")
public class ParticipationController {

    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    // Tous les projets auxquels un utilisateur participe
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Participation>> findByUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(participationService.findByUtilisateur(utilisateurId));
    }

    // Tous les participants inscrits sur un projet donné
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<Participation>> findByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(participationService.findByProjet(projetId));
    }

    // Inscription d'un utilisateur à un projet
    @PostMapping
    public ResponseEntity<Participation> create(@RequestBody Participation participation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(participationService.save(participation));
    }

    // Désinscription d'un projet
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
