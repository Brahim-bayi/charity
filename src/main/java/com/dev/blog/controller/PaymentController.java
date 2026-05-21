package com.dev.blog.controller;

import com.dev.blog.dto.PaymentIntentRequest;
import com.dev.blog.dto.PaymentIntentResponse;
import com.dev.blog.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Intègre Stripe pour le traitement des paiements en ligne.
 * Flux : le frontend crée un intent → Stripe confirme le paiement → Stripe notifie via webhook.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Crée un PaymentIntent Stripe et retourne le clientSecret au frontend.
     * Le frontend utilise ce secret avec Stripe.js pour finaliser le paiement côté client.
     */
    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @RequestBody @Valid PaymentIntentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPaymentIntent(request));
    }

    /**
     * Reçoit les événements Stripe (paiement réussi, remboursement, etc.).
     * La signature Stripe-Signature est vérifiée dans le service pour garantir l'authenticité.
     * Cet endpoint doit rester exclu du filtre CSRF car il est appelé par les serveurs Stripe.
     */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String stripeSignature) {
        paymentService.handleWebhook(payload, stripeSignature);
        return ResponseEntity.ok("OK");
    }
}