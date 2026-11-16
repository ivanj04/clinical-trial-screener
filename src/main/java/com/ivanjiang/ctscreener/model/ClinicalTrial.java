package com.ivanjiang.ctscreener.model;

import com.ivanjiang.ctscreener.model.enums.TrialPhase;
import com.ivanjiang.ctscreener.model.enums.TrialStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clinical_trials")
public class ClinicalTrial {

    @Id
    private String id;

    @Indexed(unique = true)
    private String trialId;

    private String name;
    private TrialPhase phase;
    private TrialStatus status;
    private String sponsor;

    @Builder.Default
    private List<Criterion> inclusionCriteria = new ArrayList<>();

    @Builder.Default
    private List<Criterion> exclusionCriteria = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
