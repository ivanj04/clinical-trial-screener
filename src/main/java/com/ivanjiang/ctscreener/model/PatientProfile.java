package com.ivanjiang.ctscreener.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patient_profiles")
public class PatientProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String patientId;

    private int age;
    private String gender;

    @Builder.Default
    private List<String> diagnoses = new ArrayList<>();

    @Builder.Default
    private Map<String, Double> labValues = new HashMap<>();

    @Builder.Default
    private List<String> currentMedications = new ArrayList<>();

    private int ecogScore;

    @CreatedDate
    private Instant createdAt;
}
