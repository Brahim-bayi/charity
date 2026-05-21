package com.dev.blog.exception;

/**
 * Lancée quand on tente de créer une ressource qui viole une contrainte d'unicité.
 * Le GlobalExceptionHandler la traduit en HTTP 409 Conflict.
 * Ex : inscription avec un email déjà utilisé.
 */
public class DuplicateResourceException extends RuntimeException {

    // Ex : "Utilisateur avec email 'test@example.com' existe déjà"
    public DuplicateResourceException(String ressource, String champ, String valeur) {
        super(ressource + " avec " + champ + " '" + valeur + "' existe déjà");
    }
}
