package com.ivanjiang.ctscreener.repository;

import com.ivanjiang.ctscreener.model.ClinicalTrial;
import com.ivanjiang.ctscreener.model.enums.TrialStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicalTrialRepository extends MongoRepository<ClinicalTrial, String> {
    Optional<ClinicalTrial> findByTrialId(String trialId);
    boolean existsByTrialId(String trialId);
    List<ClinicalTrial> findByStatus(TrialStatus status);
}
