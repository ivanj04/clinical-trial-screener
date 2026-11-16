package com.ivanjiang.ctscreener.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "screening_results")
public class ScreeningResult {

    @Id
    private String id;

    @Indexed
    private String trialId;

    @Indexed
    private String patientId;

    private boolean eligible;

    @Builder.Default
    private List<String> matchedInclusion = new ArrayList<>();

    @Builder.Default
    private List<String> failedExclusion = new ArrayList<>();

    private Instant screenedAt;
}
