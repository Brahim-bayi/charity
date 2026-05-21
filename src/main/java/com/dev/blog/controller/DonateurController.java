package com.dev.blog.controller;

import com.dev.blog.entity.Don;
import com.dev.blog.entity.Donateur;
import com.dev.blog.service.DonateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Gère les profils donateurs et expose leurs statistiques de générosité.
 */
@RestController
@RequestMapping("/api/donateurs")
public class DonateurController {

    private final DonateurService donateurService;

    public DonateurController(DonateurService donateurService) {
        this.donateurService = donateurService;
    }

    @GetMapping
    public ResponseEntity<List<Donateur>> findAll() {
        return ResponseEntity.ok(donateurService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donateur> findById(@PathVariable Long id) {
        return ResponseEntity.ok(donateurService.findById(id));
    }

    // Retourne l'historique complet des dons d'un donateur
    @GetMapping("/{id}/dons")
    public ResponseEntity<List<Don>> findDons(@PathVariable Long id) {
        return ResponseEntity.ok(donateurService.findDonsByDonateur(id));
    }

    // Retourne la somme cumulée de tous les dons effectués par ce donateur
    @GetMapping("/{id}/total-dons")
    public ResponseEntity<BigDecimal> totalDons(@PathVariable Long id) {
        return ResponseEntity.ok(donateurService.totalDonsByDonateur(id));
    }

    @PostMapping
    public ResponseEntity<Donateur> create(@Valid @RequestBody Donateur donateur) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donateurService.save(donateur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Donateur> update(@PathVariable Long id, @Valid @RequestBody Donateur donateur) {
        return ResponseEntity.ok(donateurService.update(id, donateur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donateurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}