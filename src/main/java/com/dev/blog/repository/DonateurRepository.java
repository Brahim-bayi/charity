package com.dev.blog.repository;

import com.dev.blog.entity.Donateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonateurRepository extends JpaRepository<Donateur, Long> {

    Optional<Donateur> findByUtilisateurId(Long utilisateurId);

    boolean existsByUtilisateurId(Long utilisateurId);
}
