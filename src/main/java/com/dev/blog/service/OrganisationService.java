package com.dev.blog.service;

import com.dev.blog.entity.Organisation;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    @Transactional(readOnly = true)
    public List<Organisation> findAll() { return organisationRepository.findAll(); }

    @Transactional(readOnly = true)
    public Organisation findById(Long id) {
        return organisationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", id));
    }

    @Transactional(readOnly = true)
    public List<Organisation> findByStatut(Organisation.Statut statut) {
        return organisationRepository.findByStatut(statut);
    }

    public Organisation save(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    public Organisation update(Long id, Organisation updated) {
        Organisation existing = findById(id);
        existing.setNom(updated.getNom());
        existing.setAdresseLegale(updated.getAdresseLegale());
        existing.setNumeroFiscal(updated.getNumeroFiscal());
        existing.setContactPrincipal(updated.getContactPrincipal());
        existing.setLogo(updated.getLogo());
        existing.setDescriptionMissions(updated.getDescriptionMissions());
        return organisationRepository.save(existing);
    }

    public Organisation valider(Long id, Organisation.Statut statut) {
        Organisation org = findById(id);
        org.setStatut(statut);
        return organisationRepository.save(org);
    }

    public void delete(Long id) {
        findById(id);
        organisationRepository.deleteById(id);
    }
}
