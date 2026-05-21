package com.dev.blog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Corps de la requête POST /api/payments/create-intent.
 * Le montant est en MAD (dirham marocain) avec un minimum de 1.00.
 * Le donateurId et projetId sont requis pour créer le Don associé au paiement Stripe.
 */
public record PaymentIntentRequest(
        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "1.00", message = "Le montant minimum est 1 MAD")
        BigDecimal montant,

        @NotNull(message = "L'identifiant du donateur est obligatoire")
        Long donateurId,

        @NotNull(message = "L'identifiant du projet est obligatoire")
        Long projetId,

        String commentaire  // optionnel — message libre du donateur
) {}
