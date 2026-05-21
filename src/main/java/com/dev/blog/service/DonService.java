package com.dev.blog.service;

import com.dev.blog.entity.Don;
import com.dev.blog.exception.BusinessException;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.DonRepository;
import com.dev.blog.repository.ProjetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DonService {

    static final String STRIPE = "STRIPE";

    private final DonRepository donRepository;
    private final ProjetRepository projetRepository;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public Page<Don> findAll(Pageable pageable) {
        return donRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Don findById(Long id) {
        return donRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Don", id));
    }

    @Transactional(readOnly = true)
    public List<Don> findByDonateur(Long donateurId) {
        return donRepository.findByDonateurId(donateurId);
    }

    @Transactional(readOnly = true)
    public List<Don> findByProjet(Long projetId) {
        return donRepository.findByProjetId(projetId);
    }

    public Don save(Don don) {
        if (don.getDonateur() == null) {
            throw new BusinessException("Le donateur est obligatoire pour enregistrer un don");
        }
        if (don.getProjet() == null) {
            throw new BusinessException("Le projet est obligatoire pour enregistrer un don");
        }
        if (don.getDateDon() == null) {
            don.setDateDon(LocalDateTime.now());
        }
        Don saved = donRepository.save(don);
        updateMontantCollecteProjet(saved.getProjet().getId());

        // Stripe payments are confirmed via webhook — avoid duplicate emails
        if (!STRIPE.equals(saved.getModePaiement())) {
            envoyerEmailConfirmation(saved);
            verifierObjectifAtteint(saved.getProjet().getId());
        }

        return saved;
    }

    public Don update(Long id, Don updated) {
        Don existing = findById(id);
        existing.setMontant(updated.getMontant());
        existing.setModePaiement(updated.getModePaiement());
        existing.setCommentaire(updated.getCommentaire());
        Don saved = donRepository.save(existing);
        updateMontantCollecteProjet(saved.getProjet().getId());
        return saved;
    }

    public void delete(Long id) {
        Don don = findById(id);
        Long projetId = don.getProjet().getId();
        donRepository.deleteById(id);
        updateMontantCollecteProjet(projetId);
    }

    void updateMontantCollecteProjet(Long projetId) {
        projetRepository.findById(projetId).ifPresent(projet -> {
            projet.setMontantCollecte(donRepository.sumMontantByProjetId(projetId));
            projetRepository.save(projet);
        });
    }

    void envoyerEmailConfirmation(Don don) {
        try {
            String email = don.getDonateur().getUtilisateur().getEmail();
            String nom = don.getDonateur().getUtilisateur().getPrenom()
                    + " " + don.getDonateur().getUtilisateur().getNom();
            emailService.envoyerConfirmationDon(email, nom, don.getMontant(), don.getProjet().getNom());
        } catch (Exception e) {
            log.warn("Erreur envoi email confirmation pour don {} : {}", don.getId(), e.getMessage());
        }
    }

    void verifierObjectifAtteint(Long projetId) {
        projetRepository.findById(projetId).ifPresent(projet -> {
            if (projet.getMontantCollecte() != null
                    && projet.getObjectifMontant() != null
                    && projet.getMontantCollecte().compareTo(projet.getObjectifMontant()) >= 0) {
                emailService.envoyerObjectifAtteint(projet.getNom(), projet.getObjectifMontant());
            }
        });
    }
}
