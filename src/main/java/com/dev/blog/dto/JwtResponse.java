package com.dev.blog.dto;

/**
 * Réponse retournée après une authentification réussie.
 * Le frontend stocke le token et le préfixe avec "Bearer " dans le header Authorization.
 * Le rôle est inclus pour que le frontend puisse adapter l'affichage sans décoder le JWT.
 */
public record JwtResponse(
        String token,
        String type,   // toujours "Bearer"
        String email,
        String role
) {
    // Constructeur raccourci : le type "Bearer" est fixé automatiquement
    public JwtResponse(String token, String email, String role) {
        this(token, "Bearer", email, role);
    }
}
