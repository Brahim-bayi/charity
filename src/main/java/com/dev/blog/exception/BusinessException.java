package com.dev.blog.exception;

/**
 * Lancée pour les violations de règles métier qui ne correspondent pas à un 404 ou 409.
 * Le GlobalExceptionHandler la traduit en HTTP 400 Bad Request.
 * Ex : tenter d'archiver un projet déjà archivé, valider un don sur un projet terminé.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
