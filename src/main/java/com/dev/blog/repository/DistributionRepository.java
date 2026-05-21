package com.dev.blog.repository;

import com.dev.blog.entity.Distribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Long> {

    List<Distribution> findByBeneficiaireId(Long beneficiaireId);

    List<Distribution> findByProjetId(Long projetId);

    long countByProjetId(Long projetId);

    @Query("SELECT COALESCE(SUM(d.montant), 0) FROM Distribution d WHERE d.projet.id = :projetId")
    BigDecimal sumMontantByProjetId(Long projetId);

    @Query("SELECT COALESCE(SUM(d.montant), 0) FROM Distribution d WHERE d.beneficiaire.id = :beneficiaireId")
    BigDecimal sumMontantByBeneficiaireId(Long beneficiaireId);
}
