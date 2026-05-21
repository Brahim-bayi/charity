package com.dev.blog.service;

import com.dev.blog.entity.Participation;
import com.dev.blog.exception.BusinessException;
import com.dev.blog.exception.ResourceNotFoundException;
import com.dev.blog.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final MessageSource messageSource;

    @Transactional(readOnly = true)
    public List<Participation> findByUtilisateur(Long utilisateurId) {
        return participationRepository.findByUtilisateurId(utilisateurId);
    }

    @Transactional(readOnly = true)
    public List<Participation> findByProjet(Long projetId) {
        return participationRepository.findByProjetId(projetId);
    }

    public Participation save(Participation participation) {
        boolean exists = participationRepository.existsByUtilisateurIdAndProjetId(
                participation.getUtilisateur().getId(), participation.getProjet().getId());
        if (exists) throw new BusinessException(msg("participation.already.exists"));
        return participationRepository.save(participation);
    }

    public void delete(Long id) {
        if (!participationRepository.existsById(id))
            throw new ResourceNotFoundException("Participation", id);
        participationRepository.deleteById(id);
    }

    private String msg(String key, Object... args) {
        return messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
    }
}
