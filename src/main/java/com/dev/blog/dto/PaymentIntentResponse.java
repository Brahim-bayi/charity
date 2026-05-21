package com.dev.blog.dto;

/**
 * Réponse retournée après création d'un PaymentIntent Stripe.
 * Le frontend utilise clientSecret + publishableKey avec Stripe.js pour confirmer le paiement.
 * Le donId permet au frontend de suivre l'état du don sans attendre le webhook.
 */
public record PaymentIntentResponse(
        String clientSecret,      // secret transmis à Stripe.js pour finaliser le paiement
        String paymentIntentId,   // identifiant Stripe pour le suivi côté backend
        String publishableKey,    // clé publique Stripe (non sensible) pour initialiser Stripe.js
        Long donId                // identifiant du Don créé en base, en attente de confirmation
) {}
