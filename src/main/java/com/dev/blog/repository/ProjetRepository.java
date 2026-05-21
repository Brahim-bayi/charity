package com.dev.blog.repository;

import com.dev.blog.entity.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

    List<Projet> findByStatut(Projet.StatutProjet statut);

    List<Projet> findByNomContainingIgnoreCase(String nom);

    List<Projet> findByCategorie(Projet.Categorie categorie);

    List<Projet> findTop10ByOrderByMontantCollecteDesc();
}
