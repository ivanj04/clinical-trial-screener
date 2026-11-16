package com.ivanjiang.ctscreener.repository;

import com.ivanjiang.ctscreener.model.PatientProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientProfileRepository extends MongoRepository<PatientProfile, String> {
    Optional<PatientProfile> findByPatientId(String patientId);
    boolean existsByPatientId(String patientId);
}
