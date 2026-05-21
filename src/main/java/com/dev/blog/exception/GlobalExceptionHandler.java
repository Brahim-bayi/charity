package com.dev.blog.exception;

import com.dev.blog.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Intercepte toutes les exceptions non gérées dans les controllers et les convertit
 * en réponses JSON structurées (ErrorResponse) avec le bon code HTTP.
 * Centralise la gestion d'erreurs pour éviter les try/catch dans chaque controller.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // Utilisé pour récupérer les messages d'erreur depuis messages.properties (i18n)
    private final MessageSource messageSource;

    // Ressource introuvable en base → 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    // Violation de contrainte d'unicité → 409 Conflict
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    // Règle métier violée → 400 Bad Request
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
    }

    /**
     * Validation des champs (@Valid) échouée → 400 Bad Request avec le détail champ par champ.
     * La liste "erreurs" dans ErrorResponse est remplie ici (null pour les autres cas).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldError> erreurs = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, msg("error.validation"), req, erreurs);
    }

    // JSON mal formé dans le body (syntaxe invalide) → 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, msg("error.json.malformed"), req, null);
    }

    // Type incompatible dans un @PathVariable ou @RequestParam (ex: texte à la place d'un Long) → 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, msg("error.type.mismatch", ex.getValue(), ex.getName()), req, null);
    }

    // Filet de sécurité pour toute exception non anticipée → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, msg("error.internal"), req, null);
    }

    // Résout une clé i18n depuis messages.properties en tenant compte de la locale de la requête
    private String msg(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }

    // Construit la réponse ErrorResponse uniforme avec timestamp, statut, chemin et erreurs
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                 HttpServletRequest req,
                                                 List<ErrorResponse.FieldError> erreurs) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                erreurs
        );
        return ResponseEntity.status(status).body(body);
    }
}
