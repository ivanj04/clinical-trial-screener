package com.ivanjiang.ctscreener.dto;

import com.ivanjiang.ctscreener.model.Criterion;
import com.ivanjiang.ctscreener.model.enums.TrialPhase;
import com.ivanjiang.ctscreener.model.enums.TrialStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateTrialRequest {
    @NotBlank
    private String trialId;

    @NotBlank
    private String name;

    @NotNull
    private TrialPhase phase;

    @NotNull
    private TrialStatus status;

    private String sponsor;

    private List<Criterion> inclusionCriteria = new ArrayList<>();
    private List<Criterion> exclusionCriteria = new ArrayList<>();
}
