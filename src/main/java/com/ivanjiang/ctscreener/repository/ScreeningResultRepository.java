package com.ivanjiang.ctscreener.repository;

import com.ivanjiang.ctscreener.model.ScreeningResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningResultRepository extends MongoRepository<ScreeningResult, String> {
    List<ScreeningResult> findByPatientId(String patientId);
    List<ScreeningResult> findByTrialId(String trialId);
    List<ScreeningResult> findByPatientIdAndTrialId(String patientId, String trialId);
}
