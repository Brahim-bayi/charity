package com.dev.blog.repository;

import com.dev.blog.entity.Beneficiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiaireRepository extends JpaRepository<Beneficiaire, Long> {

    Optional<Beneficiaire> findByUtilisateurId(Long utilisateurId);

    boolean existsByUtilisateurId(Long utilisateurId);
}
