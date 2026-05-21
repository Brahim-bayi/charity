package com.dev.blog.controller;

import com.dev.blog.entity.Distribution;
import com.dev.blog.service.DistributionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gère les distributions d'aide : enregistre quand un projet distribue des ressources à un bénéficiaire.
 * Une distribution lie un projet, un bénéficiaire et un montant/quantité distribuée.
 */
@RestController
@RequestMapping("/api/distributions")
public class DistributionController {

    private final DistributionService distributionService;

    public DistributionController(DistributionService distributionService) {
        this.distributionService = distributionService;
    }

    @GetMapping
    public ResponseEntity<List<Distribution>> findAll() {
        return ResponseEntity.ok(distributionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Distribution> findById(@PathVariable Long id) {
        return ResponseEntity.ok(distributionService.findById(id));
    }

    // Toutes les aides reçues par un bénéficiaire donné
    @GetMapping("/beneficiaire/{beneficiaireId}")
    public ResponseEntity<List<Distribution>> findByBeneficiaire(@PathVariable Long beneficiaireId) {
        return ResponseEntity.ok(distributionService.findByBeneficiaire(beneficiaireId));
    }

    // Toutes les distributions effectuées depuis un projet donné
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<Distribution>> findByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(distributionService.findByProjet(projetId));
    }

    @PostMapping
    public ResponseEntity<Distribution> create(@RequestBody Distribution distribution) {
        return ResponseEntity.status(HttpStatus.CREATED).body(distributionService.save(distribution));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Distribution> update(@PathVariable Long id, @RequestBody Distribution distribution) {
        return ResponseEntity.ok(distributionService.update(id, distribution));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        distributionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}