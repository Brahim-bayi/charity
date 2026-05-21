package com.dev.blog.service;

import com.dev.blog.entity.Distribution;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.DistributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DistributionService {

    private final DistributionRepository distributionRepository;

    @Transactional(readOnly = true)
    public List<Distribution> findAll() {
        return distributionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Distribution findById(Long id) {
        return distributionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distribution", id));
    }

    @Transactional(readOnly = true)
    public List<Distribution> findByBeneficiaire(Long beneficiaireId) {
        return distributionRepository.findByBeneficiaireId(beneficiaireId);
    }

    @Transactional(readOnly = true)
    public List<Distribution> findByProjet(Long projetId) {
        return distributionRepository.findByProjetId(projetId);
    }

    public Distribution save(Distribution distribution) {
        if (distribution.getDateDistribution() == null) {
            distribution.setDateDistribution(LocalDateTime.now());
        }
        return distributionRepository.save(distribution);
    }

    public Distribution update(Long id, Distribution updated) {
        Distribution existing = findById(id);
        existing.setMontant(updated.getMontant());
        existing.setDescription(updated.getDescription());
        existing.setTypeAide(updated.getTypeAide());
        return distributionRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        distributionRepository.deleteById(id);
    }
}
