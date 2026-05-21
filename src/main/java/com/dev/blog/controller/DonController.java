package com.dev.blog.controller;

import com.dev.blog.entity.Don;
import com.dev.blog.service.DonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gère les dons effectués par les donateurs vers les projets.
 * La liste principale est paginée pour éviter des réponses trop lourdes.
 */
@RestController
@RequestMapping("/api/dons")
public class DonController {

    private final DonService donService;

    public DonController(DonService donService) {
        this.donService = donService;
    }

    // Paginé par défaut : 10 dons par page, triés par date croissante
    @GetMapping
    public ResponseEntity<Page<Don>> findAll(
            @PageableDefault(size = 10, sort = "dateDon") Pageable pageable) {
        return ResponseEntity.ok(donService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Don> findById(@PathVariable Long id) {
        return ResponseEntity.ok(donService.findById(id));
    }

    // Utile pour afficher l'historique de dons d'un donateur sur son tableau de bord
    @GetMapping("/donateur/{donateurId}")
    public ResponseEntity<List<Don>> findByDonateur(@PathVariable Long donateurId) {
        return ResponseEntity.ok(donService.findByDonateur(donateurId));
    }

    // Utile pour afficher tous les dons reçus par un projet
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<Don>> findByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(donService.findByProjet(projetId));
    }

    @PostMapping
    public ResponseEntity<Don> create(@RequestBody Don don) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donService.save(don));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Don> update(@PathVariable Long id, @RequestBody Don don) {
        return ResponseEntity.ok(donService.update(id, don));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donService.delete(id);
        return ResponseEntity.noContent().build();
    }
}