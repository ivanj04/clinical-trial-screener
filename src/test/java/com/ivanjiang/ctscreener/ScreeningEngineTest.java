package com.ivanjiang.ctscreener;

import com.ivanjiang.ctscreener.model.Criterion;
import com.ivanjiang.ctscreener.model.PatientProfile;
import com.ivanjiang.ctscreener.model.enums.CriterionType;
import com.ivanjiang.ctscreener.model.enums.Operator;
import com.ivanjiang.ctscreener.service.ScreeningEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ScreeningEngineTest {

    private ScreeningEngine engine;
    private PatientProfile patient;

    @BeforeEach
    void setUp() {
        engine = new ScreeningEngine();
        patient = PatientProfile.builder()
                .patientId("TEST-001")
                .age(45)
                .gender("FEMALE")
                .diagnoses(List.of("NSCLC", "STAGE_III"))
                .labValues(Map.of("HGB", 12.5, "WBC", 5.0))
                .currentMedications(List.of("METFORMIN"))
                .ecogScore(1)
                .build();
    }

    @Test
    void ageMin_pass() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.AGE_MIN)
                .operator(Operator.GREATER_THAN_OR_EQUAL)
                .value("18")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isTrue();
    }

    @Test
    void ageMin_fail() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.AGE_MIN)
                .operator(Operator.GREATER_THAN_OR_EQUAL)
                .value("60")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isFalse();
    }

    @Test
    void ageMax_pass() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.AGE_MAX)
                .operator(Operator.LESS_THAN_OR_EQUAL)
                .value("75")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isTrue();
    }

    @Test
    void diagnosis_contains_pass() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.DIAGNOSIS)
                .operator(Operator.CONTAINS)
                .value("NSCLC")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isTrue();
    }

    @Test
    void diagnosis_contains_fail() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.DIAGNOSIS)
                .operator(Operator.CONTAINS)
                .value("MELANOMA")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isFalse();
    }

    @Test
    void medication_exclusion_triggers() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.MEDICATION_EXCLUSION)
                .operator(Operator.CONTAINS)
                .value("METFORMIN")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isTrue();
    }

    @Test
    void labValue_pass() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.LAB_VALUE)
                .operator(Operator.GREATER_THAN_OR_EQUAL)
                .value("HGB:10.0")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isTrue();
    }

    @Test
    void labValue_missing_key_fails() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.LAB_VALUE)
                .operator(Operator.GREATER_THAN_OR_EQUAL)
                .value("CREATININE:1.5")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isFalse();
    }

    @Test
    void gender_equals_pass() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.GENDER)
                .operator(Operator.EQUALS)
                .value("FEMALE")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isTrue();
    }

    @Test
    void ecogScore_pass() {
        Criterion c = Criterion.builder()
                .criterionId("C1")
                .type(CriterionType.ECOG_SCORE)
                .operator(Operator.LESS_THAN_OR_EQUAL)
                .value("2")
                .build();
        assertThat(engine.evaluateCriterion(c, patient)).isTrue();
    }

    @Test
    void nullCriterion_returnsFalse() {
        assertThat(engine.evaluateCriterion(null, patient)).isFalse();
    }
}
