package com.dev.blog.repository;

import com.dev.blog.entity.Don;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonRepository extends JpaRepository<Don, Long> {

    @EntityGraph(attributePaths = {"donateur", "donateur.utilisateur", "projet"})
    Page<Don> findAll(Pageable pageable);

    List<Don> findByDonateurId(Long donateurId);

    List<Don> findByProjetId(Long projetId);

    long countByProjetId(Long projetId);

    Optional<Don> findByStripePaymentIntentId(String stripePaymentIntentId);

    @Query("SELECT COALESCE(SUM(d.montant), 0) FROM Don d WHERE d.projet.id = :projetId")
    BigDecimal sumMontantByProjetId(Long projetId);

    @Query("SELECT COALESCE(SUM(d.montant), 0) FROM Don d WHERE d.donateur.id = :donateurId")
    BigDecimal sumMontantByDonateurId(Long donateurId);

    @Query("SELECT DISTINCT d.donateur.utilisateur.email FROM Don d WHERE d.projet.id = :projetId")
    List<String> findEmailsDonateursByProjetId(Long projetId);
}
