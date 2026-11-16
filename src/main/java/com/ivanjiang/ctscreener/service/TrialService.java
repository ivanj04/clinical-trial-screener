package com.ivanjiang.ctscreener.service;

import com.ivanjiang.ctscreener.dto.CreateTrialRequest;
import com.ivanjiang.ctscreener.model.ClinicalTrial;
import com.ivanjiang.ctscreener.model.enums.TrialStatus;
import com.ivanjiang.ctscreener.repository.ClinicalTrialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrialService {

    private final ClinicalTrialRepository trialRepository;

    public ClinicalTrial createTrial(CreateTrialRequest request) {
        if (trialRepository.existsByTrialId(request.getTrialId())) {
            throw new IllegalArgumentException("Trial ID already exists: " + request.getTrialId());
        }

        ClinicalTrial trial = ClinicalTrial.builder()
                .trialId(request.getTrialId())
                .name(request.getName())
                .phase(request.getPhase())
                .status(request.getStatus())
                .sponsor(request.getSponsor())
                .inclusionCriteria(request.getInclusionCriteria())
                .exclusionCriteria(request.getExclusionCriteria())
                .build();

        return trialRepository.save(trial);
    }

    public Page<ClinicalTrial> listTrials(Pageable pageable) {
        return trialRepository.findAll(pageable);
    }

    public ClinicalTrial getTrialByTrialId(String trialId) {
        return trialRepository.findByTrialId(trialId)
                .orElseThrow(() -> new IllegalArgumentException("Trial not found: " + trialId));
    }

    public ClinicalTrial updateTrial(String trialId, CreateTrialRequest request) {
        ClinicalTrial trial = getTrialByTrialId(trialId);

        trial.setName(request.getName());
        trial.setPhase(request.getPhase());
        trial.setStatus(request.getStatus());
        trial.setSponsor(request.getSponsor());
        trial.setInclusionCriteria(request.getInclusionCriteria());
        trial.setExclusionCriteria(request.getExclusionCriteria());

        return trialRepository.save(trial);
    }

    public List<ClinicalTrial> getRecruitingTrials() {
        return trialRepository.findByStatus(TrialStatus.RECRUITING);
    }
}
