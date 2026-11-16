package com.ivanjiang.ctscreener.service;

import com.ivanjiang.ctscreener.model.Criterion;
import com.ivanjiang.ctscreener.model.PatientProfile;
import com.ivanjiang.ctscreener.model.enums.CriterionType;
import com.ivanjiang.ctscreener.model.enums.Operator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Core rules engine: evaluates individual eligibility criteria against patient data.
 * Each criterion is evaluated independently; callers aggregate results.
 */
@Slf4j
@Service
public class ScreeningEngine {

    /**
     * Evaluate a single criterion against a patient profile.
     *
     * @param criterion the eligibility rule to evaluate
     * @param patient   the patient data
     * @return true if the criterion is met (passes)
     */
    public boolean evaluateCriterion(Criterion criterion, PatientProfile patient) {
        if (criterion == null || criterion.getType() == null || criterion.getValue() == null) {
            log.warn("Skipping null/incomplete criterion");
            return false;
        }

        CriterionType type = criterion.getType();
        String rawValue = criterion.getValue();

        try {
            return switch (type) {
                case AGE_MIN -> evaluateNumeric(patient.getAge(), Operator.GREATER_THAN_OR_EQUAL, rawValue);
                case AGE_MAX -> evaluateNumeric(patient.getAge(), Operator.LESS_THAN_OR_EQUAL, rawValue);
                case ECOG_SCORE -> evaluateNumeric(patient.getEcogScore(), criterion.getOperator(), rawValue);
                case GENDER -> evaluateGender(patient.getGender(), criterion.getOperator(), rawValue);
                case DIAGNOSIS -> evaluateListCriterion(patient.getDiagnoses(), criterion.getOperator(), rawValue);
                case MEDICATION_EXCLUSION -> evaluateListCriterion(patient.getCurrentMedications(), criterion.getOperator(), rawValue);
                case LAB_VALUE -> evaluateLabValue(patient, criterion);
            };
        } catch (NumberFormatException e) {
            log.error("Failed to parse numeric value '{}' for criterion {}: {}",
                    rawValue, criterion.getCriterionId(), e.getMessage());
            return false;
        }
    }

    // --- Private evaluation helpers ---

    private boolean evaluateNumeric(double patientValue, Operator operator, String rawValue) {
        double threshold = Double.parseDouble(rawValue);
        if (operator == null) operator = Operator.EQUALS;
        return switch (operator) {
            case EQUALS -> patientValue == threshold;
            case GREATER_THAN -> patientValue > threshold;
            case LESS_THAN -> patientValue < threshold;
            case GREATER_THAN_OR_EQUAL -> patientValue >= threshold;
            case LESS_THAN_OR_EQUAL -> patientValue <= threshold;
            default -> false;
        };
    }

    private boolean evaluateGender(String patientGender, Operator operator, String requiredGender) {
        if (patientGender == null) return false;
        boolean matches = patientGender.equalsIgnoreCase(requiredGender);
        if (operator == null || operator == Operator.EQUALS) return matches;
        if (operator == Operator.NOT_CONTAINS) return !matches;
        return matches;
    }

    private boolean evaluateListCriterion(java.util.List<String> patientList, Operator operator, String value) {
        if (patientList == null) return false;
        boolean contains = patientList.stream()
                .anyMatch(item -> item.equalsIgnoreCase(value));
        if (operator == null) return contains;
        return switch (operator) {
            case CONTAINS, EQUALS -> contains;
            case NOT_CONTAINS -> !contains;
            default -> contains;
        };
    }

    private boolean evaluateLabValue(PatientProfile patient, Criterion criterion) {
        // Criterion value format: "LAB_NAME:threshold"  e.g. "HGB:12.0"
        String rawValue = criterion.getValue();
        String[] parts = rawValue.split(":", 2);
        if (parts.length != 2) {
            log.warn("LAB_VALUE criterion '{}' has invalid format; expected 'LAB_NAME:threshold'",
                    criterion.getCriterionId());
            return false;
        }
        String labName = parts[0].trim().toUpperCase(Locale.ROOT);
        Double patientLabValue = patient.getLabValues().get(labName);
        if (patientLabValue == null) {
            log.debug("Patient {} has no lab value for {}", patient.getPatientId(), labName);
            return false;
        }
        return evaluateNumeric(patientLabValue, criterion.getOperator(), parts[1].trim());
    }
}
