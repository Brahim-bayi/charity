package com.dev.blog.service;

import com.dev.blog.entity.Don;
import com.dev.blog.entity.Donateur;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.DonateurRepository;
import com.dev.blog.repository.DonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DonateurService {

    private final DonateurRepository donateurRepository;
    private final DonRepository donRepository;

    @Transactional(readOnly = true)
    public List<Donateur> findAll() {
        return donateurRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Donateur findById(Long id) {
        return donateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donateur", id));
    }

    @Transactional(readOnly = true)
    public List<Don> findDonsByDonateur(Long donateurId) {
        findById(donateurId);
        return donRepository.findByDonateurId(donateurId);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalDonsByDonateur(Long donateurId) {
        findById(donateurId);
        return donRepository.sumMontantByDonateurId(donateurId);
    }

    public Donateur save(Donateur donateur) {
        return donateurRepository.save(donateur);
    }

    public Donateur update(Long id, Donateur updated) {
        Donateur existing = findById(id);
        existing.setTypeOrganisation(updated.getTypeOrganisation());
        existing.setDescription(updated.getDescription());
        return donateurRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        donateurRepository.deleteById(id);
    }
}
