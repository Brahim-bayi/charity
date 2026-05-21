package com.dev.blog.exception;

/**
 * Lancée quand une entité demandée n'existe pas en base.
 * Le GlobalExceptionHandler la traduit automatiquement en réponse HTTP 404.
 * Deux constructeurs disponibles : recherche par id numérique, ou par un champ texte (ex: email).
 */
public class ResourceNotFoundException extends RuntimeException {

    // Ex : "Utilisateur introuvable avec l'id : 42"
    public ResourceNotFoundException(String ressource, Long id) {
        super(ressource + " introuvable avec l'id : " + id);
    }

    // Ex : "Utilisateur introuvable avec email : test@example.com"
    public ResourceNotFoundException(String ressource, String champ, String valeur) {
        super(ressource + " introuvable avec " + champ + " : " + valeur);
    }
}
