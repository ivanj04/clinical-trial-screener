package com.ivanjiang.ctscreener.service;

import com.ivanjiang.ctscreener.dto.CreatePatientRequest;
import com.ivanjiang.ctscreener.model.PatientProfile;
import com.ivanjiang.ctscreener.repository.PatientProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientProfileRepository patientRepository;

    public PatientProfile createPatient(CreatePatientRequest request) {
        if (patientRepository.existsByPatientId(request.getPatientId())) {
            throw new IllegalArgumentException("Patient ID already exists: " + request.getPatientId());
        }

        PatientProfile patient = PatientProfile.builder()
                .patientId(request.getPatientId())
                .age(request.getAge())
                .gender(request.getGender())
                .diagnoses(request.getDiagnoses())
                .labValues(request.getLabValues())
                .currentMedications(request.getCurrentMedications())
                .ecogScore(request.getEcogScore())
                .build();

        return patientRepository.save(patient);
    }

    public PatientProfile getPatientByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
    }

    public PatientProfile updatePatient(String patientId, CreatePatientRequest request) {
        PatientProfile patient = getPatientByPatientId(patientId);

        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setDiagnoses(request.getDiagnoses());
        patient.setLabValues(request.getLabValues());
        patient.setCurrentMedications(request.getCurrentMedications());
        patient.setEcogScore(request.getEcogScore());

        return patientRepository.save(patient);
    }
}
