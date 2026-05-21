package com.dev.blog.controller;

import com.dev.blog.entity.Beneficiaire;
import com.dev.blog.entity.Distribution;
import com.dev.blog.service.BeneficiaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Gère les bénéficiaires : personnes ou familles qui reçoivent de l'aide via les projets.
 */
@RestController
@RequestMapping("/api/beneficiaires")
public class BeneficiaireController {

    private final BeneficiaireService beneficiaireService;

    public BeneficiaireController(BeneficiaireService beneficiaireService) {
        this.beneficiaireService = beneficiaireService;
    }

    @GetMapping
    public ResponseEntity<List<Beneficiaire>> findAll() {
        return ResponseEntity.ok(beneficiaireService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Beneficiaire> findById(@PathVariable Long id) {
        return ResponseEntity.ok(beneficiaireService.findById(id));
    }

    // Retourne toutes les distributions (aides reçues) pour ce bénéficiaire
    @GetMapping("/{id}/distributions")
    public ResponseEntity<List<Distribution>> findDistributions(@PathVariable Long id) {
        return ResponseEntity.ok(beneficiaireService.findDistributionsByBeneficiaire(id));
    }

    // Somme totale de l'aide reçue par ce bénéficiaire sur tous les projets
    @GetMapping("/{id}/total-aide")
    public ResponseEntity<BigDecimal> totalAide(@PathVariable Long id) {
        return ResponseEntity.ok(beneficiaireService.totalAideRecue(id));
    }

    @PostMapping
    public ResponseEntity<Beneficiaire> create(@RequestBody Beneficiaire beneficiaire) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beneficiaireService.save(beneficiaire));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Beneficiaire> update(@PathVariable Long id, @RequestBody Beneficiaire beneficiaire) {
        return ResponseEntity.ok(beneficiaireService.update(id, beneficiaire));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        beneficiaireService.delete(id);
        return ResponseEntity.noContent().build();
    }
}