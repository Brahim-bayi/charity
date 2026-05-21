package com.dev.blog.controller;

import com.dev.blog.dto.JwtResponse;
import com.dev.blog.dto.LoginRequest;
import com.dev.blog.entity.Beneficiaire;
import com.dev.blog.entity.Donateur;
import com.dev.blog.entity.Utilisateur;
import com.dev.blog.security.JwtUtils;
import com.dev.blog.service.BeneficiaireService;
import com.dev.blog.service.DonateurService;
import com.dev.blog.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Gère l'authentification : connexion par email/mot de passe et inscription.
 * Retourne un token JWT à utiliser dans le header Authorization des requêtes suivantes.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification")
public class AuthController {

    // Valeur par défaut pour les donateurs qui s'inscrivent sans préciser leur type d'organisation
    private static final String DEFAULT_TYPE_ORGANISATION = "Particulier";

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UtilisateurService utilisateurService;
    private final DonateurService donateurService;
    private final BeneficiaireService beneficiaireService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                          UtilisateurService utilisateurService, DonateurService donateurService,
                          BeneficiaireService beneficiaireService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.utilisateurService = utilisateurService;
        this.donateurService = donateurService;
        this.beneficiaireService = beneficiaireService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authentifie l'utilisateur avec email + mot de passe.
     * Retourne un token JWT signé ainsi que le rôle pour que le frontend puisse adapter l'affichage.
     */
    @Operation(summary = "Connexion — retourne un token JWT")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.motDePasse())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);
        Utilisateur utilisateur = utilisateurService.findByEmail(request.email());
        return ResponseEntity.ok(new JwtResponse(token, utilisateur.getEmail(), utilisateur.getRole().name()));
    }

    /**
     * Inscrit un nouvel utilisateur et crée automatiquement son profil métier selon son rôle :
     * - DONATEUR  → crée un Donateur avec typeOrganisation = "Particulier" par défaut
     * - BENEFICIAIRE → crée un Beneficiaire lié à ce compte
     * Le mot de passe est hashé avant la persistance (BCrypt).
     */
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    @PostMapping("/register")
    public ResponseEntity<Utilisateur> register(@Valid @RequestBody Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        Utilisateur saved = utilisateurService.save(utilisateur);

        if (saved.getRole() == Utilisateur.Role.DONATEUR) {
            donateurService.save(Donateur.builder()
                    .utilisateur(saved)
                    .typeOrganisation(DEFAULT_TYPE_ORGANISATION)
                    .build());
        } else if (saved.getRole() == Utilisateur.Role.BENEFICIAIRE) {
            beneficiaireService.save(Beneficiaire.builder()
                    .utilisateur(saved)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}