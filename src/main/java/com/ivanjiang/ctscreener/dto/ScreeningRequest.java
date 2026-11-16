package com.ivanjiang.ctscreener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScreeningRequest {
    @NotBlank
    private String trialId;

    @NotBlank
    private String patientId;
}
