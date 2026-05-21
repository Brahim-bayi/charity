package com.dev.blog.service;

import com.dev.blog.entity.Utilisateur;
import com.dev.blog.exception.DuplicateResourceException;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
    }

    @Transactional(readOnly = true)
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    public Utilisateur save(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new DuplicateResourceException("Utilisateur", "email", utilisateur.getEmail());
        }
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur update(Long id, Utilisateur updated) {
        Utilisateur existing = findById(id);
        existing.setNom(updated.getNom());
        existing.setPrenom(updated.getPrenom());
        existing.setTelephone(updated.getTelephone());
        existing.setAdresse(updated.getAdresse());
        existing.setRole(updated.getRole());
        return utilisateurRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        utilisateurRepository.deleteById(id);
    }
}
