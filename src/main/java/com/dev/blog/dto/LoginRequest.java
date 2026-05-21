package com.dev.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Corps de la requête POST /api/auth/login.
 * Les contraintes de validation sont vérifiées avant d'atteindre le controller (@Valid).
 */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String motDePasse
) {}
