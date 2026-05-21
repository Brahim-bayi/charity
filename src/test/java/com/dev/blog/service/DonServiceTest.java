package com.dev.blog.service;

import com.dev.blog.entity.Don;
import com.dev.blog.entity.Donateur;
import com.dev.blog.entity.Projet;
import com.dev.blog.exception.BusinessException;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.DonRepository;
import com.dev.blog.repository.ProjetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonServiceTest {

    @Mock
    private DonRepository donRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private DonService donService;

    private Don donValide;
    private Donateur donateur;
    private Projet projet;

    @BeforeEach
    void setUp() {
        donateur = Donateur.builder().id(1L).build();
        projet = Projet.builder()
                .id(1L)
                .nom("Aide alimentaire")
                .montantCollecte(BigDecimal.ZERO)
                .build();
        donValide = Don.builder()
                .id(1L)
                .montant(new BigDecimal("500.00"))
                .dateDon(LocalDateTime.now())
                .donateur(donateur)
                .projet(projet)
                .build();
    }

    // ------------------------------------------------------------------ findAll
    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("retourne la liste de tous les dons")
        void retourneTousLesDons() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Don> page = new PageImpl<>(List.of(donValide), pageable, 1);
            when(donRepository.findAll(pageable)).thenReturn(page);

            Page<Don> result = donService.findAll(pageable);

            assertThat(result.getContent()).hasSize(1).containsExactly(donValide);
            verify(donRepository).findAll(pageable);
        }

        @Test
        @DisplayName("retourne une page vide quand il n'y a pas de dons")
        void retournePageVide() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Don> page = new PageImpl<>(List.of(), pageable, 0);
            when(donRepository.findAll(pageable)).thenReturn(page);

            assertThat(donService.findAll(pageable).getContent()).isEmpty();
        }
    }

    // ------------------------------------------------------------------ findById
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("retourne le don quand l'id existe")
        void retourneDonExistant() {
            when(donRepository.findById(1L)).thenReturn(Optional.of(donValide));

            Don result = donService.findById(1L);

            assertThat(result).isEqualTo(donValide);
        }

        @Test
        @DisplayName("lève ResourceNotFoundException quand l'id est inconnu")
        void leveExceptionIdInconnu() {
            when(donRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> donService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ------------------------------------------------------------------ findByDonateur / findByProjet
    @Nested
    @DisplayName("findByDonateur() / findByProjet()")
    class FindByFiltres {

        @Test
        @DisplayName("retourne les dons d'un donateur")
        void retourneDonsParDonateur() {
            when(donRepository.findByDonateurId(1L)).thenReturn(List.of(donValide));

            assertThat(donService.findByDonateur(1L)).containsExactly(donValide);
        }

        @Test
        @DisplayName("retourne les dons d'un projet")
        void retourneDonsParProjet() {
            when(donRepository.findByProjetId(1L)).thenReturn(List.of(donValide));

            assertThat(donService.findByProjet(1L)).containsExactly(donValide);
        }
    }

    // ------------------------------------------------------------------ save
    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("enregistre le don et met à jour montantCollecte du projet")
        void enregistreDonEtMajProjet() {
            Don donSansDate = Don.builder()
                    .montant(new BigDecimal("200.00"))
                    .donateur(donateur)
                    .projet(projet)
                    .build();
            when(donRepository.save(any(Don.class))).thenAnswer(inv -> {
                Don d = inv.getArgument(0);
                d.setId(2L);
                return d;
            });
            when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
            when(donRepository.sumMontantByProjetId(1L)).thenReturn(new BigDecimal("200.00"));

            Don result = donService.save(donSansDate);

            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getDateDon()).isNotNull();
            verify(projetRepository).save(projet);
        }

        @Test
        @DisplayName("lève BusinessException quand le donateur est null")
        void leveExceptionSansDonateur() {
            Don donSansDonateur = Don.builder()
                    .montant(new BigDecimal("100.00"))
                    .projet(projet)
                    .build();

            assertThatThrownBy(() -> donService.save(donSansDonateur))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("donateur");

            verify(donRepository, never()).save(any());
        }

        @Test
        @DisplayName("lève BusinessException quand le projet est null")
        void leveExceptionSansProjet() {
            Don donSansProjet = Don.builder()
                    .montant(new BigDecimal("100.00"))
                    .donateur(donateur)
                    .build();

            assertThatThrownBy(() -> donService.save(donSansProjet))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("projet");

            verify(donRepository, never()).save(any());
        }

        @Test
        @DisplayName("conserve la dateDon si elle est déjà renseignée")
        void conserveDateDonExistante() {
            LocalDateTime dateFixee = LocalDateTime.of(2026, 1, 15, 10, 0);
            donValide.setDateDon(dateFixee);
            when(donRepository.save(any())).thenReturn(donValide);
            when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
            when(donRepository.sumMontantByProjetId(1L)).thenReturn(donValide.getMontant());

            Don result = donService.save(donValide);

            assertThat(result.getDateDon()).isEqualTo(dateFixee);
        }
    }

    // ------------------------------------------------------------------ update
    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("met à jour les champs modifiables du don")
        void metsAJourDon() {
            Don modifications = Don.builder()
                    .montant(new BigDecimal("999.00"))
                    .modePaiement("Virement")
                    .commentaire("Don annuel")
                    .build();
            when(donRepository.findById(1L)).thenReturn(Optional.of(donValide));
            when(donRepository.save(any())).thenReturn(donValide);
            when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
            when(donRepository.sumMontantByProjetId(1L)).thenReturn(new BigDecimal("999.00"));

            Don result = donService.update(1L, modifications);

            assertThat(result.getMontant()).isEqualByComparingTo("999.00");
            assertThat(result.getModePaiement()).isEqualTo("Virement");
            assertThat(result.getCommentaire()).isEqualTo("Don annuel");
        }

        @Test
        @DisplayName("lève ResourceNotFoundException si le don n'existe pas")
        void leveExceptionDonInexistant() {
            when(donRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> donService.update(99L, donValide))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ------------------------------------------------------------------ delete
    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("supprime le don et met à jour montantCollecte")
        void supprimeDonEtMajProjet() {
            when(donRepository.findById(1L)).thenReturn(Optional.of(donValide));
            when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
            when(donRepository.sumMontantByProjetId(1L)).thenReturn(BigDecimal.ZERO);

            donService.delete(1L);

            verify(donRepository).deleteById(1L);
            verify(projetRepository).save(projet);
        }

        @Test
        @DisplayName("lève ResourceNotFoundException si le don n'existe pas")
        void leveExceptionDonInexistant() {
            when(donRepository.findById(42L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> donService.delete(42L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("42");

            verify(donRepository, never()).deleteById(any());
        }
    }
}
