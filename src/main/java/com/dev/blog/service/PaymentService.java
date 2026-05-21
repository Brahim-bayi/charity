package com.dev.blog.service;

import com.dev.blog.dto.PaymentIntentRequest;
import com.dev.blog.dto.PaymentIntentResponse;
import com.dev.blog.entity.Don;
import com.dev.blog.entity.Donateur;
import com.dev.blog.entity.Projet;
import com.dev.blog.exception.BusinessException;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.DonateurRepository;
import com.dev.blog.repository.DonRepository;
import com.dev.blog.repository.ProjetRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.publishable.key}")
    private String publishableKey;

    private final DonRepository donRepository;
    private final DonateurRepository donateurRepository;
    private final ProjetRepository projetRepository;
    private final DonService donService;

    @PostConstruct
    private void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) {

        Donateur donateur = donateurRepository.findById(request.donateurId())
                .orElseThrow(() -> new ResourceNotFoundException("Donateur", request.donateurId()));
        Projet projet = projetRepository.findById(request.projetId())
                .orElseThrow(() -> new ResourceNotFoundException("Projet", request.projetId()));

        try {
            long montantEnCentimes = request.montant().multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(montantEnCentimes)
                    .setCurrency("mad")
                    .setDescription("Don pour le projet : " + projet.getNom())
                    .putMetadata("donateurId", String.valueOf(donateur.getId()))
                    .putMetadata("projetId", String.valueOf(projet.getId()))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Don don = Don.builder()
                    .montant(request.montant())
                    .dateDon(LocalDateTime.now())
                    .modePaiement(DonService.STRIPE)
                    .commentaire(request.commentaire())
                    .donateur(donateur)
                    .projet(projet)
                    .statutPaiement(Don.StatutPaiement.EN_ATTENTE)
                    .stripePaymentIntentId(intent.getId())
                    .build();
            Don saved = donRepository.save(don);

            return new PaymentIntentResponse(intent.getClientSecret(), intent.getId(), publishableKey, saved.getId());
        } catch (StripeException e) {
            log.error("Erreur Stripe : {}", e.getMessage());
            throw new BusinessException("Erreur lors du traitement du paiement : " + e.getMessage());
        }
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new BusinessException("Signature webhook invalide");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded"      -> processPaymentSucceeded(event);
            case "payment_intent.payment_failed" -> processPaymentFailed(event);
            default -> log.debug("Événement Stripe ignoré : {}", event.getType());
        }
    }

    private void processPaymentSucceeded(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(stripeObject instanceof PaymentIntent intent)) return;

        donRepository.findByStripePaymentIntentId(intent.getId()).ifPresent(don -> {
            don.setStatutPaiement(Don.StatutPaiement.CONFIRME);
            Don saved = donRepository.save(don);
            donService.updateMontantCollecteProjet(saved.getProjet().getId());
            donService.verifierObjectifAtteint(saved.getProjet().getId());
            donService.envoyerEmailConfirmation(saved);
        });
    }

    private void processPaymentFailed(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(stripeObject instanceof PaymentIntent intent)) return;

        donRepository.findByStripePaymentIntentId(intent.getId()).ifPresent(don -> {
            don.setStatutPaiement(Don.StatutPaiement.ECHEC);
            donRepository.save(don);
            log.info("Paiement échoué pour le don id={}", don.getId());
        });
    }
}
