package com.dev.blog.repository;

import com.dev.blog.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByUtilisateurId(Long utilisateurId);
    List<Participation> findByProjetId(Long projetId);
    boolean existsByUtilisateurIdAndProjetId(Long utilisateurId, Long projetId);
}
