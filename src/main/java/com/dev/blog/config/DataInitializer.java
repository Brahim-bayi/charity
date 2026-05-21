package com.dev.blog.config;

import com.dev.blog.entity.*;
import com.dev.blog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final DonateurRepository donateurRepository;
    private final BeneficiaireRepository beneficiaireRepository;
    private final ProjetRepository projetRepository;
    private final DonRepository donRepository;
    private final DistributionRepository distributionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean alreadySeeded =
                projetRepository.count()       > 0 ||
                donateurRepository.count()     > 0 ||
                beneficiaireRepository.count() > 0;

        if (alreadySeeded) return;

        // ── 1 Admin ───────────────────────────────────────────────────
        Utilisateur admin = creerUtilisateur(
                "Admin", "Système", "admin@charity.ma", "admin123", Utilisateur.Role.ADMIN);

        // ── 2 Donateurs ───────────────────────────────────────────────
        Utilisateur uDon1 = creerUtilisateur(
                "Ahmed", "Benali", "ahmed@charity.ma", "pass123", Utilisateur.Role.DONATEUR);
        Utilisateur uDon2 = creerUtilisateur(
                "Fatima", "Ouali", "fatima@charity.ma", "pass123", Utilisateur.Role.DONATEUR);

        // ── 2 Bénéficiaires ───────────────────────────────────────────
        Utilisateur uBen1 = creerUtilisateur(
                "Khadija", "Mansouri", "khadija@charity.ma", "pass123", Utilisateur.Role.BENEFICIAIRE);
        Utilisateur uBen2 = creerUtilisateur(
                "Hassan", "Tahiri", "hassan@charity.ma", "pass123", Utilisateur.Role.BENEFICIAIRE);

        utilisateurRepository.saveAll(List.of(admin, uDon1, uDon2, uBen1, uBen2));

        // ── Donateurs ─────────────────────────────────────────────────
        Donateur d1 = Donateur.builder()
                .utilisateur(uDon1)
                .typeOrganisation("Particulier")
                .description("Donateur régulier depuis 2022")
                .build();
        Donateur d2 = Donateur.builder()
                .utilisateur(uDon2)
                .typeOrganisation("Entreprise")
                .description("PME engagée dans la RSE")
                .build();
        donateurRepository.saveAll(List.of(d1, d2));

        // ── Bénéficiaires ─────────────────────────────────────────────
        Beneficiaire b1 = Beneficiaire.builder()
                .utilisateur(uBen1)
                .besoins("Aide alimentaire, scolarité des enfants")
                .situationFamiliale("Veuve")
                .nombreEnfants(3)
                .build();
        Beneficiaire b2 = Beneficiaire.builder()
                .utilisateur(uBen2)
                .besoins("Soins médicaux, aide au loyer")
                .situationFamiliale("Marié")
                .nombreEnfants(5)
                .build();
        beneficiaireRepository.saveAll(List.of(b1, b2));

        // ── 1 Projet "Aide Ramadan 2026" ──────────────────────────────
        Projet projet = Projet.builder()
                .nom("Aide Ramadan 2026")
                .description("Distribution de couffins alimentaires et d'aides financières "
                        + "aux familles démunies durant le mois de Ramadan 2026")
                .objectifMontant(new BigDecimal("50000.00"))
                .montantCollecte(BigDecimal.ZERO)
                .dateDebut(LocalDate.of(2026, 3, 1))
                .dateFin(LocalDate.of(2026, 3, 31))
                .statut(Projet.StatutProjet.EN_COURS)
                .build();
        projetRepository.save(projet);

        // ── 3 Dons ────────────────────────────────────────────────────
        Don don1 = don(new BigDecimal("8000.00"),  d1, projet, "Virement",  "Don mensuel Ramadan");
        Don don2 = don(new BigDecimal("15000.00"), d2, projet, "Chèque",    "Contribution entreprise");
        Don don3 = don(new BigDecimal("3500.00"),  d1, projet, "Espèces",   "Complément don personnel");
        donRepository.saveAll(List.of(don1, don2, don3));

        projet.setMontantCollecte(new BigDecimal("26500.00"));
        projetRepository.save(projet);

        // ── 2 Distributions ───────────────────────────────────────────
        Distribution dist1 = distribution(
                new BigDecimal("2000.00"), b1, projet,
                "Couffin alimentaire complet + aide scolaire", "Alimentaire");
        Distribution dist2 = distribution(
                new BigDecimal("2500.00"), b2, projet,
                "Couffin alimentaire + aide médicale", "Alimentaire");
        distributionRepository.saveAll(List.of(dist1, dist2));
    }

    private Utilisateur creerUtilisateur(String nom, String prenom, String email,
                                          String motDePasse, Utilisateur.Role role) {
        return Utilisateur.builder()
                .nom(nom).prenom(prenom).email(email)
                .motDePasse(passwordEncoder.encode(motDePasse))
                .role(role)
                .build();
    }

    private Don don(BigDecimal montant, Donateur donateur, Projet projet,
                    String modePaiement, String commentaire) {
        return Don.builder()
                .montant(montant)
                .dateDon(LocalDateTime.now())
                .modePaiement(modePaiement)
                .commentaire(commentaire)
                .donateur(donateur)
                .projet(projet)
                .build();
    }

    private Distribution distribution(BigDecimal montant, Beneficiaire beneficiaire,
                                       Projet projet, String description, String typeAide) {
        return Distribution.builder()
                .montant(montant)
                .dateDistribution(LocalDateTime.now())
                .description(description)
                .typeAide(typeAide)
                .beneficiaire(beneficiaire)
                .projet(projet)
                .build();
    }
}
