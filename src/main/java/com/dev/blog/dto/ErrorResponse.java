package com.dev.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Structure uniforme retournée par le GlobalExceptionHandler pour toutes les erreurs API.
 * Le champ "erreurs" n'est inclus dans la réponse JSON que pour les erreurs de validation
 * (@JsonInclude NON_NULL évite d'afficher "erreurs: null" pour les autres types d'erreurs).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,       // libellé HTTP (ex: "Not Found", "Bad Request")
        String message,     // message métier lisible par le frontend
        String path,        // URL appelée — utile pour le débogage
        List<FieldError> erreurs  // détail champ par champ, présent uniquement si validation échoue
) {
    // Représente une erreur de validation sur un champ spécifique du body
    public record FieldError(String champ, String message) {}
}
