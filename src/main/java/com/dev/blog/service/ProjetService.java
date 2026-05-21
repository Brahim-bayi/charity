package com.dev.blog.service;

import com.dev.blog.dto.ProjetProgressionDTO;
import com.dev.blog.dto.ProjetStatistiquesDTO;
import com.dev.blog.entity.Distribution;
import com.dev.blog.entity.Don;
import com.dev.blog.entity.Projet;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.DistributionRepository;
import com.dev.blog.repository.DonRepository;
import com.dev.blog.repository.ProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final DonRepository donRepository;
    private final DistributionRepository distributionRepository;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public List<Projet> findAll() {
        return projetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Projet findById(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet", id));
    }

    @Transactional(readOnly = true)
    public List<Projet> findByStatut(Projet.StatutProjet statut) {
        return projetRepository.findByStatut(statut);
    }

    @Transactional(readOnly = true)
    public List<Projet> rechercher(String nom) {
        return projetRepository.findByNomContainingIgnoreCase(nom);
    }

    @Transactional(readOnly = true)
    public List<Projet> findByCategorie(Projet.Categorie categorie) {
        return projetRepository.findByCategorie(categorie);
    }

    @Transactional(readOnly = true)
    public List<Projet> findPopulaires() {
        return projetRepository.findTop10ByOrderByMontantCollecteDesc();
    }

    public Projet archiver(Long id) {
        Projet projet = findById(id);
        projet.setStatut(Projet.StatutProjet.ARCHIVE);
        return projetRepository.save(projet);
    }

    @Transactional(readOnly = true)
    public List<Don> findDonsByProjet(Long projetId) {
        findById(projetId);
        return donRepository.findByProjetId(projetId);
    }

    @Transactional(readOnly = true)
    public List<Distribution> findDistributionsByProjet(Long projetId) {
        findById(projetId);
        return distributionRepository.findByProjetId(projetId);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalDonsCollectes(Long projetId) {
        return donRepository.sumMontantByProjetId(projetId);
    }

    @Transactional(readOnly = true)
    public ProjetStatistiquesDTO getStatistiques(Long projetId) {
        Projet projet = findById(projetId);
        BigDecimal totalDons = donRepository.sumMontantByProjetId(projetId);
        long nombreDons = donRepository.countByProjetId(projetId);
        long nombreDistributions = distributionRepository.countByProjetId(projetId);
        BigDecimal totalDistributions = distributionRepository.sumMontantByProjetId(projetId);
        return new ProjetStatistiquesDTO(
                projet.getId(),
                projet.getNom(),
                projet.getObjectifMontant(),
                totalDons,
                nombreDons,
                nombreDistributions,
                totalDistributions
        );
    }

    @Transactional(readOnly = true)
    public ProjetProgressionDTO getProgression(Long projetId) {
        Projet projet = findById(projetId);
        BigDecimal collecte = projet.getMontantCollecte() != null
                ? projet.getMontantCollecte() : BigDecimal.ZERO;
        double pourcentage = projet.getObjectifMontant().compareTo(BigDecimal.ZERO) > 0
                ? collecte.divide(projet.getObjectifMontant(), 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;
        return new ProjetProgressionDTO(
                projet.getId(), projet.getNom(),
                projet.getObjectifMontant(), collecte,
                Math.min(pourcentage, 100.0));
    }

    public Projet save(Projet projet) {
        if (projet.getMontantCollecte() == null) {
            projet.setMontantCollecte(BigDecimal.ZERO);
        }
        return projetRepository.save(projet);
    }

    public Projet update(Long id, Projet updated) {
        Projet existing = findById(id);
        boolean statutChanged = !existing.getStatut().equals(updated.getStatut());
        Projet.StatutProjet ancienStatut = existing.getStatut();

        existing.setNom(updated.getNom());
        existing.setDescription(updated.getDescription());
        existing.setLieu(updated.getLieu());
        existing.setObjectifMontant(updated.getObjectifMontant());
        existing.setDateDebut(updated.getDateDebut());
        existing.setDateFin(updated.getDateFin());
        existing.setStatut(updated.getStatut());
        existing.setCategorie(updated.getCategorie());
        existing.setOrganisation(updated.getOrganisation());
        existing.setImageUrl(updated.getImageUrl());
        existing.setVideoUrl(updated.getVideoUrl());
        Projet saved = projetRepository.save(existing);

        if (statutChanged) {
            List<String> emails = donRepository.findEmailsDonateursByProjetId(id);
            emailService.envoyerMiseAJourProjet(
                    emails, saved.getNom(), ancienStatut.name(), saved.getStatut().name());
        }

        return saved;
    }

    public void delete(Long id) {
        findById(id);
        projetRepository.deleteById(id);
    }
}
