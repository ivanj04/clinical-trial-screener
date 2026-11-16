package com.ivanjiang.ctscreener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BatchScreeningRequest {
    @NotBlank
    private String patientId;
}
