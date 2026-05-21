package com.dev.blog.controller;

import com.dev.blog.entity.Utilisateur;
import com.dev.blog.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gère les comptes utilisateurs (admins, donateurs, bénéficiaires).
 * La création d'un compte via cet endpoint ne crée pas le profil métier associé —
 * utiliser /api/auth/register pour une inscription complète avec profil.
 */
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public ResponseEntity<List<Utilisateur>> findAll() {
        return ResponseEntity.ok(utilisateurService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> findById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.findById(id));
    }

    // Recherche par email — utilisé pour vérifier l'unicité ou retrouver un compte
    @GetMapping("/email/{email}")
    public ResponseEntity<Utilisateur> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(utilisateurService.findByEmail(email));
    }

    @PostMapping
    public ResponseEntity<Utilisateur> create(@RequestBody Utilisateur utilisateur) {
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurService.save(utilisateur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> update(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        return ResponseEntity.ok(utilisateurService.update(id, utilisateur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        utilisateurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
