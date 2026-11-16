package com.ivanjiang.ctscreener.service;

import com.ivanjiang.ctscreener.model.ClinicalTrial;
import com.ivanjiang.ctscreener.model.Criterion;
import com.ivanjiang.ctscreener.model.PatientProfile;
import com.ivanjiang.ctscreener.model.ScreeningResult;
import com.ivanjiang.ctscreener.repository.ScreeningResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final TrialService trialService;
    private final PatientService patientService;
    private final ScreeningEngine engine;
    private final ScreeningResultRepository resultRepository;

    /**
     * Screen a single patient against one specific trial.
     */
    public ScreeningResult screen(String trialId, String patientId) {
        ClinicalTrial trial = trialService.getTrialByTrialId(trialId);
        PatientProfile patient = patientService.getPatientByPatientId(patientId);
        return evaluate(trial, patient);
    }

    /**
     * Batch-screen one patient against every RECRUITING trial.
     */
    public List<ScreeningResult> batchScreen(String patientId) {
        PatientProfile patient = patientService.getPatientByPatientId(patientId);
        List<ClinicalTrial> recruitingTrials = trialService.getRecruitingTrials();

        List<ScreeningResult> results = new ArrayList<>();
        for (ClinicalTrial trial : recruitingTrials) {
            results.add(evaluate(trial, patient));
        }
        return results;
    }

    /**
     * Retrieve all historical screening results for a patient.
     */
    public List<ScreeningResult> getResultsForPatient(String patientId) {
        return resultRepository.findByPatientId(patientId);
    }

    // --- Core evaluation logic ---

    private ScreeningResult evaluate(ClinicalTrial trial, PatientProfile patient) {
        List<String> matchedInclusion = new ArrayList<>();
        List<String> failedExclusion = new ArrayList<>();

        // Check all inclusion criteria — patient must meet ALL
        boolean allInclusionPassed = true;
        for (Criterion criterion : trial.getInclusionCriteria()) {
            boolean passed = engine.evaluateCriterion(criterion, patient);
            if (passed) {
                matchedInclusion.add(describeCriterion(criterion));
            } else {
                allInclusionPassed = false;
                log.debug("Patient {} failed inclusion criterion {} for trial {}",
                        patient.getPatientId(), criterion.getCriterionId(), trial.getTrialId());
            }
        }

        // Check all exclusion criteria — patient must meet NONE
        boolean noExclusionTriggered = true;
        for (Criterion criterion : trial.getExclusionCriteria()) {
            boolean triggered = engine.evaluateCriterion(criterion, patient);
            if (triggered) {
                noExclusionTriggered = false;
                failedExclusion.add(describeCriterion(criterion));
                log.debug("Patient {} triggered exclusion criterion {} for trial {}",
                        patient.getPatientId(), criterion.getCriterionId(), trial.getTrialId());
            }
        }

        boolean eligible = allInclusionPassed && noExclusionTriggered;

        ScreeningResult result = ScreeningResult.builder()
                .trialId(trial.getTrialId())
                .patientId(patient.getPatientId())
                .eligible(eligible)
                .matchedInclusion(matchedInclusion)
                .failedExclusion(failedExclusion)
                .screenedAt(Instant.now())
                .build();

        return resultRepository.save(result);
    }

    private String describeCriterion(Criterion c) {
        String desc = c.getDescription();
        if (desc != null && !desc.isBlank()) return desc;
        return String.format("[%s] %s %s %s",
                c.getCriterionId(), c.getType(), c.getOperator(), c.getValue());
    }
}
