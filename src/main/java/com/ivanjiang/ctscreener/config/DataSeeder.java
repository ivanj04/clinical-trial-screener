package com.ivanjiang.ctscreener.config;

import com.ivanjiang.ctscreener.model.Criterion;
import com.ivanjiang.ctscreener.model.ClinicalTrial;
import com.ivanjiang.ctscreener.model.PatientProfile;
import com.ivanjiang.ctscreener.model.User;
import com.ivanjiang.ctscreener.model.enums.CriterionType;
import com.ivanjiang.ctscreener.model.enums.Operator;
import com.ivanjiang.ctscreener.model.enums.TrialPhase;
import com.ivanjiang.ctscreener.model.enums.TrialStatus;
import com.ivanjiang.ctscreener.repository.ClinicalTrialRepository;
import com.ivanjiang.ctscreener.repository.PatientProfileRepository;
import com.ivanjiang.ctscreener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ClinicalTrialRepository trialRepository;
    private final PatientProfileRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedTrials();
        seedPatients();
    }

    private void seedUsers() {
        if (userRepository.existsByUsername("admin")) return;
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@ctscreener.local")
                .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                .build();
        userRepository.save(admin);
        log.info("[DataSeeder] Created admin user");
    }

    private void seedTrials() {
        if (trialRepository.existsByTrialId("NCT-BMS-001")) return;

        // Trial 1: Nivolumab NSCLC — Phase III
        ClinicalTrial trial1 = ClinicalTrial.builder()
                .trialId("NCT-BMS-001")
                .name("Nivolumab in Advanced Non-Small Cell Lung Cancer")
                .phase(TrialPhase.PHASE_III)
                .status(TrialStatus.RECRUITING)
                .sponsor("Bristol Myers Squibb")
                .inclusionCriteria(List.of(
                        Criterion.builder()
                                .criterionId("C001-INC-01")
                                .type(CriterionType.AGE_MIN)
                                .operator(Operator.GREATER_THAN_OR_EQUAL)
                                .value("18")
                                .description("Patient must be at least 18 years old")
                                .build(),
                        Criterion.builder()
                                .criterionId("C001-INC-02")
                                .type(CriterionType.DIAGNOSIS)
                                .operator(Operator.CONTAINS)
                                .value("NSCLC")
                                .description("Confirmed diagnosis of non-small cell lung cancer")
                                .build(),
                        Criterion.builder()
                                .criterionId("C001-INC-03")
                                .type(CriterionType.ECOG_SCORE)
                                .operator(Operator.LESS_THAN_OR_EQUAL)
                                .value("2")
                                .description("ECOG performance status 0-2")
                                .build()
                ))
                .exclusionCriteria(List.of(
                        Criterion.builder()
                                .criterionId("C001-EXC-01")
                                .type(CriterionType.MEDICATION_EXCLUSION)
                                .operator(Operator.CONTAINS)
                                .value("SYSTEMIC_CORTICOSTEROIDS")
                                .description("No systemic corticosteroids within 14 days")
                                .build()
                ))
                .build();
        trialRepository.save(trial1);

        // Trial 2: Ipilimumab Melanoma — Phase II
        if (!trialRepository.existsByTrialId("NCT-BMS-002")) {
            ClinicalTrial trial2 = ClinicalTrial.builder()
                    .trialId("NCT-BMS-002")
                    .name("Ipilimumab Combination Therapy in Unresectable Melanoma")
                    .phase(TrialPhase.PHASE_II)
                    .status(TrialStatus.RECRUITING)
                    .sponsor("Bristol Myers Squibb")
                    .inclusionCriteria(List.of(
                            Criterion.builder()
                                    .criterionId("C002-INC-01")
                                    .type(CriterionType.AGE_MIN)
                                    .operator(Operator.GREATER_THAN_OR_EQUAL)
                                    .value("18")
                                    .description("Age >= 18")
                                    .build(),
                            Criterion.builder()
                                    .criterionId("C002-INC-02")
                                    .type(CriterionType.AGE_MAX)
                                    .operator(Operator.LESS_THAN_OR_EQUAL)
                                    .value("75")
                                    .description("Age <= 75")
                                    .build(),
                            Criterion.builder()
                                    .criterionId("C002-INC-03")
                                    .type(CriterionType.DIAGNOSIS)
                                    .operator(Operator.CONTAINS)
                                    .value("MELANOMA")
                                    .description("Confirmed unresectable melanoma")
                                    .build(),
                            Criterion.builder()
                                    .criterionId("C002-INC-04")
                                    .type(CriterionType.LAB_VALUE)
                                    .operator(Operator.GREATER_THAN_OR_EQUAL)
                                    .value("HGB:10.0")
                                    .description("Hemoglobin >= 10 g/dL")
                                    .build()
                    ))
                    .exclusionCriteria(List.of(
                            Criterion.builder()
                                    .criterionId("C002-EXC-01")
                                    .type(CriterionType.DIAGNOSIS)
                                    .operator(Operator.CONTAINS)
                                    .value("AUTOIMMUNE_DISEASE")
                                    .description("No active autoimmune disease")
                                    .build()
                    ))
                    .build();
            trialRepository.save(trial2);
        }

        log.info("[DataSeeder] Seeded 2 clinical trials");
    }

    private void seedPatients() {
        if (patientRepository.existsByPatientId("PT-10001")) return;

        // Patient 1: eligible for NCT-BMS-001
        patientRepository.save(PatientProfile.builder()
                .patientId("PT-10001")
                .age(55)
                .gender("MALE")
                .diagnoses(List.of("NSCLC", "STAGE_IIIB"))
                .labValues(Map.of("HGB", 13.5, "WBC", 6.2, "PLT", 210.0))
                .currentMedications(List.of("METFORMIN"))
                .ecogScore(1)
                .build());

        // Patient 2: eligible for NCT-BMS-002
        if (!patientRepository.existsByPatientId("PT-10002")) {
            patientRepository.save(PatientProfile.builder()
                    .patientId("PT-10002")
                    .age(42)
                    .gender("FEMALE")
                    .diagnoses(List.of("MELANOMA", "STAGE_IV"))
                    .labValues(Map.of("HGB", 11.8, "LDH", 280.0))
                    .currentMedications(List.of("ACETAMINOPHEN"))
                    .ecogScore(0)
                    .build());
        }

        // Patient 3: ineligible (too old for Trial 2, has exclusion diagnosis)
        if (!patientRepository.existsByPatientId("PT-10003")) {
            patientRepository.save(PatientProfile.builder()
                    .patientId("PT-10003")
                    .age(80)
                    .gender("MALE")
                    .diagnoses(List.of("MELANOMA", "AUTOIMMUNE_DISEASE"))
                    .labValues(Map.of("HGB", 9.5, "WBC", 4.1))
                    .currentMedications(List.of("PREDNISONE", "SYSTEMIC_CORTICOSTEROIDS"))
                    .ecogScore(3)
                    .build());
        }

        log.info("[DataSeeder] Seeded 3 patient profiles");
    }
}
