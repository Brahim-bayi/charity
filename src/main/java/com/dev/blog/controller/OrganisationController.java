package com.dev.blog.controller;

import com.dev.blog.entity.Organisation;
import com.dev.blog.service.OrganisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gère les opérations CRUD sur les organisations caritatives.
 * Une organisation doit être validée par un admin avant de pouvoir gérer des projets.
 */
@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {

    private final OrganisationService organisationService;

    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    // Retourne toutes les organisations sans filtre de statut
    @GetMapping
    public ResponseEntity<List<Organisation>> findAll() {
        return ResponseEntity.ok(organisationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organisation> findById(@PathVariable Long id) {
        return ResponseEntity.ok(organisationService.findById(id));
    }

    // Permet de filtrer par statut : EN_ATTENTE, VALIDEE, REJETEE
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Organisation>> findByStatut(@PathVariable Organisation.Statut statut) {
        return ResponseEntity.ok(organisationService.findByStatut(statut));
    }

    @PostMapping
    public ResponseEntity<Organisation> create(@RequestBody Organisation organisation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.save(organisation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Organisation> update(@PathVariable Long id, @RequestBody Organisation organisation) {
        return ResponseEntity.ok(organisationService.update(id, organisation));
    }

    /**
     * Endpoint réservé aux admins pour valider ou rejeter une organisation.
     * Le statut passé en paramètre (VALIDEE / REJETEE) met à jour la visibilité de l'organisation.
     */
    @PatchMapping("/{id}/valider")
    public ResponseEntity<Organisation> valider(@PathVariable Long id,
                                                @RequestParam Organisation.Statut statut) {
        return ResponseEntity.ok(organisationService.valider(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        organisationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}