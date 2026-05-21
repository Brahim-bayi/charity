package com.dev.blog.service;

import com.dev.blog.entity.Beneficiaire;
import com.dev.blog.entity.Distribution;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.BeneficiaireRepository;
import com.dev.blog.repository.DistributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BeneficiaireService {

    private final BeneficiaireRepository beneficiaireRepository;
    private final DistributionRepository distributionRepository;

    @Transactional(readOnly = true)
    public List<Beneficiaire> findAll() {
        return beneficiaireRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Beneficiaire findById(Long id) {
        return beneficiaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiaire", id));
    }

    @Transactional(readOnly = true)
    public List<Distribution> findDistributionsByBeneficiaire(Long beneficiaireId) {
        findById(beneficiaireId);
        return distributionRepository.findByBeneficiaireId(beneficiaireId);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalAideRecue(Long beneficiaireId) {
        findById(beneficiaireId);
        return distributionRepository.sumMontantByBeneficiaireId(beneficiaireId);
    }

    public Beneficiaire save(Beneficiaire beneficiaire) {
        return beneficiaireRepository.save(beneficiaire);
    }

    public Beneficiaire update(Long id, Beneficiaire updated) {
        Beneficiaire existing = findById(id);
        existing.setBesoins(updated.getBesoins());
        existing.setSituationFamiliale(updated.getSituationFamiliale());
        existing.setNombreEnfants(updated.getNombreEnfants());
        return beneficiaireRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        beneficiaireRepository.deleteById(id);
    }
}
